package it.schwarz.jobs.review.coupon.common;

import java.math.BigDecimal;
import java.util.Objects;

public class AmountOfMoney {
    public static final AmountOfMoney ZERO = new AmountOfMoney(BigDecimal.ZERO);
    private final BigDecimal amount;

    private AmountOfMoney(BigDecimal amount) {
        this.amount = requireNonNegative(Objects.requireNonNull(amount, "amount"));
    }

    private static BigDecimal requireNonNegative(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monetary amount must not be negative: " + amount);
        }
        return amount;
    }

    public static AmountOfMoney of(String amountAsString) {
        return new AmountOfMoney(new BigDecimal(Objects.requireNonNull(amountAsString, "amountAsString")));
    }

    public static AmountOfMoney of(BigDecimal amountAsBigDecimal) {
        return new AmountOfMoney(amountAsBigDecimal);
    }

    public boolean isGreaterThan(AmountOfMoney otherAmount) {
        return amount.compareTo(Objects.requireNonNull(otherAmount, "otherAmount").amount) > 0;
    }

    public boolean isLessThan(AmountOfMoney otherAmount) {
        return amount.compareTo(Objects.requireNonNull(otherAmount, "otherAmount").amount) < 0;
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    public AmountOfMoney subtract(AmountOfMoney discount) {
        Objects.requireNonNull(discount, "discount");
        BigDecimal result = amount.subtract(discount.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Subtract would leave a negative amount (%s - %s = %s)"
                            .formatted(amount, discount.amount, result));
        }
        return new AmountOfMoney(result);
    }
}
