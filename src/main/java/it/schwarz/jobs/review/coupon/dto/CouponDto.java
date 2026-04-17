package it.schwarz.jobs.review.coupon.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CouponDto(
        @NotNull
        @Size(min = 1, max = 20)
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
        String description,

        @NotNull
        @Min(0)
        long applicationCount) {
}
