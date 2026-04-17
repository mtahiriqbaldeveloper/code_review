package it.schwarz.jobs.review.coupon.dto;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCouponRequestDto(

        @NotBlank
        @Size(max = 20)
        String code,

        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal discount,

        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal minBasketValue,

        @NotBlank
        @Size(max = 1000)
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
