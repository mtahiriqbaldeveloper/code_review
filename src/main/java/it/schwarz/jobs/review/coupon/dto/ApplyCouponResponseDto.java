package it.schwarz.jobs.review.coupon.dto;

import java.math.BigDecimal;

public record ApplyCouponResponseDto(

        BasketDto basket,
        BigDecimal appliedDiscount) {

    public static ApplyCouponResponseDto of(ApplicationResult applicationResult) {
        return new ApplyCouponResponseDto(new BasketDto(
                applicationResult.basket().value().toBigDecimal()),
                applicationResult.appliedCoupon().discount().toBigDecimal()
        );
    }
}
