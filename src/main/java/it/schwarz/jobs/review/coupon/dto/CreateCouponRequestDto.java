package it.schwarz.jobs.review.coupon.dto;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCouponRequestDto(

        @NotNull
        @Size(min = 1, max = 20)
        @NotBlank
        String code,

        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal discount,

        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal minBasketValue,

        @NotNull
        @Size(min = 1, max = 1000)
        @NotBlank
        String description) {

    public CouponPayload toCouponPayload() {
        return new CouponPayload(
                code,
                AmountOfMoney.of(discount),
                AmountOfMoney.of(minBasketValue),
                description
        );
    }
}
