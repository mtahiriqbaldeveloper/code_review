package it.schwarz.jobs.review.coupon.dto;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BasketDto(
        @NotNull
        @Min(0)
        @Max(10000)
        BigDecimal value
) {
    public Basket toBasket() {
        return new Basket(AmountOfMoney.of(value));
    }

}
