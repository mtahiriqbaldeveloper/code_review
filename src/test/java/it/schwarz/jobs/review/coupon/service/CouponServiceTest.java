package it.schwarz.jobs.review.coupon.service;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;
import it.schwarz.jobs.review.coupon.dto.CouponPayload;
import it.schwarz.jobs.review.coupon.entities.Coupon;
import it.schwarz.jobs.review.coupon.exception.CouponAlreadyExistsException;
import it.schwarz.jobs.review.coupon.exception.CouponNotValidException;
import it.schwarz.jobs.review.coupon.repo.ApplicationRepository;
import it.schwarz.jobs.review.coupon.repo.CouponRepository;
import it.schwarz.jobs.review.coupon.testdata.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CouponServiceTest {

    private CouponRepository coupons;
    private CouponService service;

    @BeforeEach
    void setUp() {
        coupons = mock(CouponRepository.class);
        service = new CouponService(coupons, mock(ApplicationRepository.class));
    }

    @Test
    void findAllCouponsReturnsAll() {
        when(coupons.findAllFetchingApplications()).thenReturn(List.of(
                toEntity(TestData.coupon12For20()),
                toEntity(TestData.coupon12For20())
        ));

        assertThat(service.findAllCoupons()).hasSize(2);
    }

    @Test
    void createCouponPersistsAndReturns() {
        when(coupons.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.createCoupon(TestData.coupon12For20())).isNotNull();
    }

    @Test
    void createCouponDuplicateKeyThrowsAlreadyExists() {
        var coupon = TestData.coupon12For20();
        when(coupons.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> service.createCoupon(coupon))
                .isInstanceOf(CouponAlreadyExistsException.class);
    }

    @Test
    void createCouponZeroDiscountThrowsNotValid() {
        var invalid = new CouponPayload("X", AmountOfMoney.ZERO, AmountOfMoney.of("10"), "desc");

        assertThatThrownBy(() -> service.createCoupon(invalid))
                .isInstanceOf(CouponNotValidException.class);
    }

    private static Coupon toEntity(CouponPayload c) {
        return new Coupon(c.code(), c.discount().toBigDecimal(), c.description(), c.minBasketValue().toBigDecimal());
    }
}
