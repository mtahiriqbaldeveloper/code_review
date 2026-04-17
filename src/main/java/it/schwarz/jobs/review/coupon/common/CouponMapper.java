package it.schwarz.jobs.review.coupon.common;

import it.schwarz.jobs.review.coupon.dto.CouponApplications;
import it.schwarz.jobs.review.coupon.dto.CouponPayload;
import it.schwarz.jobs.review.coupon.entities.Application;
import it.schwarz.jobs.review.coupon.entities.Coupon;

public final class CouponMapper {

    private CouponMapper() {
    }

    public static Coupon toEntity(CouponPayload payload) {
        return new Coupon(
                payload.code(),
                payload.discount().toBigDecimal(),
                payload.description(),
                payload.minBasketValue().toBigDecimal());
    }

    public static CouponPayload toPayload(Coupon coupon) {
        return new CouponPayload(
                coupon.getCode(),
                AmountOfMoney.of(coupon.getDiscount()),
                AmountOfMoney.of(coupon.getMinBasketValue()),
                coupon.getDescription(),
                coupon.getApplications().size());
    }

    public static CouponApplications toApplications(Coupon coupon) {
        return new CouponApplications(
                coupon.getCode(),
                coupon.getApplications().stream().map(Application::getTimestamp).toList());
    }
}
