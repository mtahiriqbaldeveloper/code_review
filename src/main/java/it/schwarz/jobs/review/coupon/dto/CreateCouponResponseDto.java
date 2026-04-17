package it.schwarz.jobs.review.coupon.dto;

public record CreateCouponResponseDto(CouponDto coupon) {

    public static CreateCouponResponseDto of(CouponPayload coupon) {
        return new CreateCouponResponseDto(
                new CouponDto(
                        coupon.code(),
                        coupon.discount().toBigDecimal(),
                        coupon.minBasketValue().toBigDecimal(),
                        coupon.description(),
                        coupon.applicationCount()));
    }
}
