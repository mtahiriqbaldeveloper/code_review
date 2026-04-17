package it.schwarz.jobs.review.coupon.dto;

import java.util.List;

public record GetCouponsResponseDto(List<CouponDto> coupons) {

    public static GetCouponsResponseDto of(List<CouponPayload> coupons) {
        return new GetCouponsResponseDto(
                coupons.stream()
                        .map(coupon -> new CouponDto(
                                coupon.code(),
                                coupon.discount().toBigDecimal(),
                                coupon.minBasketValue().toBigDecimal(),
                                coupon.description(),
                                coupon.applicationCount()))
                        .toList());
    }
}
