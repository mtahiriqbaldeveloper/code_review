package it.schwarz.jobs.review.coupon.service;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;
import it.schwarz.jobs.review.coupon.common.CouponMapper;
import it.schwarz.jobs.review.coupon.dto.ApplicationResult;
import it.schwarz.jobs.review.coupon.dto.Basket;
import it.schwarz.jobs.review.coupon.dto.CouponApplications;
import it.schwarz.jobs.review.coupon.dto.CouponPayload;
import it.schwarz.jobs.review.coupon.entities.Application;
import it.schwarz.jobs.review.coupon.entities.Coupon;
import it.schwarz.jobs.review.coupon.exception.BasketValueTooLowException;
import it.schwarz.jobs.review.coupon.exception.CouponAlreadyExistsException;
import it.schwarz.jobs.review.coupon.exception.CouponCodeNotFoundException;
import it.schwarz.jobs.review.coupon.exception.CouponNotValidException;
import it.schwarz.jobs.review.coupon.repo.ApplicationRepository;
import it.schwarz.jobs.review.coupon.repo.CouponRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CouponService {

    private static final String NOT_FOUND = "Coupon-Code %s not found.";

    private final CouponRepository coupons;
    private final ApplicationRepository applications;

    public CouponService(CouponRepository coupons, ApplicationRepository applications) {
        this.coupons = coupons;
        this.applications = applications;
    }

    public CouponPayload createCoupon(CouponPayload coupon) {
        validateCoupon(coupon);
        try {
            return CouponMapper.toPayload(coupons.save(CouponMapper.toEntity(coupon)));
        } catch (DataIntegrityViolationException ex) {
            throw new CouponAlreadyExistsException("Coupon already exists: " + coupon.code());
        }
    }

    public List<CouponPayload> findAllCoupons() {
        return coupons.findAllFetchingApplications().stream().map(CouponMapper::toPayload).toList();
    }

    public CouponApplications getApplications(String couponCode) {
        return coupons.findByCodeFetchingApplications(couponCode)
                .map(CouponMapper::toApplications)
                .orElseThrow(() -> new CouponCodeNotFoundException(NOT_FOUND.formatted(couponCode)));
    }

    public ApplicationResult applyCoupon(Basket basket, String couponCode) {
        Coupon coupon = coupons.findByCode(couponCode)
                .orElseThrow(() -> new CouponCodeNotFoundException(NOT_FOUND.formatted(couponCode)));

        CouponPayload couponPayload = CouponMapper.toPayload(coupon);

        validateCoupon(couponPayload);
        AmountOfMoney value = basket.value();
        validateBasketValue(value, couponPayload);

        Application application = new Application(Instant.now());
        application.setCoupon(coupon);
        applications.save(application);

        return new ApplicationResult(new Basket(value.subtract(couponPayload.discount())), couponPayload);
    }

    private static void validateBasketValue(AmountOfMoney basketValue, CouponPayload coupon) {
        if (basketValue.isLessThan(coupon.discount())) {
            throw new BasketValueTooLowException(
                    "The basket value (%s) must not be less than the discount (%s)."
                            .formatted(basketValue.toBigDecimal(), coupon.discount().toBigDecimal()));
        }
        if (basketValue.isLessThan(coupon.minBasketValue())) {
            throw new BasketValueTooLowException(
                    "The basket value (%s) must not be less than the min. allowed basket value (%s)."
                            .formatted(basketValue.toBigDecimal(), coupon.minBasketValue().toBigDecimal()));
        }
    }

    private static void validateCoupon(CouponPayload coupon) {
        if (!coupon.discount().isGreaterThan(AmountOfMoney.ZERO)) {
            throw new CouponNotValidException(
                    "Coupon %s is not valid. Discount (%s) must be greater than zero."
                            .formatted(coupon.code(), coupon.discount().toBigDecimal()));
        }
        if (coupon.minBasketValue().isLessThan(AmountOfMoney.ZERO)) {
            throw new CouponNotValidException(
                    "Coupon %s is not valid. Min. basket value (%s) must not be negative."
                            .formatted(coupon.code(), coupon.minBasketValue().toBigDecimal()));
        }
    }
}
