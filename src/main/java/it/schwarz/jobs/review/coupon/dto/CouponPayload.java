package it.schwarz.jobs.review.coupon.dto;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;

public record CouponPayload(String code, AmountOfMoney discount, AmountOfMoney minBasketValue, String description,
                            long applicationCount) {

    public CouponPayload(String code, AmountOfMoney discount, AmountOfMoney minBasketValue, String description) {
        this(code, discount, minBasketValue, description, 0);
    }
}
