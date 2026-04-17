package it.schwarz.jobs.review.coupon.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AmountOfMoneyTest {

    private static final AmountOfMoney REFERENCE = AmountOfMoney.of("1.23");

    @Test
    void zeroConstant() {
        assertThat(AmountOfMoney.ZERO.toBigDecimal()).isZero();
    }

    @ParameterizedTest
    @CsvSource({
            "1.22, true, false",
            "1.00, true, false",
            "0, true, false",
            "1.23, false, false",
            "1.3, false, true"
    })
    void comparesToReference(String other, boolean greater, boolean less) {
        var otherAmount = AmountOfMoney.of(other);
        assertThat(REFERENCE.isGreaterThan(otherAmount)).isEqualTo(greater);
        assertThat(REFERENCE.isLessThan(otherAmount)).isEqualTo(less);
    }

    @Test
    void createsFromBigDecimal() {
        assertThat(AmountOfMoney.of(new BigDecimal("4.56")).toBigDecimal())
                .isEqualByComparingTo("4.56");
    }

    @Test
    void subtracts() {
        assertThat(AmountOfMoney.of("10.00").subtract(AmountOfMoney.of("2.50")).toBigDecimal())
                .isEqualByComparingTo("7.50");
    }

    @Test
    void rejectsNegativeAmounts() {
        assertThatIllegalArgumentException().isThrownBy(() -> AmountOfMoney.of("-1.23"));
        assertThatIllegalArgumentException().isThrownBy(() -> AmountOfMoney.of(new BigDecimal("-0.01")));
    }

    @Test
    void subtractCannotYieldNegativeBalance() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> AmountOfMoney.of("1.00").subtract(AmountOfMoney.of("2.00")))
                .withMessageContaining("Subtract would leave a negative amount")
                .withMessageContaining("-1.00");
    }

    @Test
    void rejectsNullArguments() {
        assertThatNullPointerException().isThrownBy(() -> AmountOfMoney.of((String) null));
        assertThatNullPointerException().isThrownBy(() -> AmountOfMoney.of((BigDecimal) null));
        assertThatNullPointerException().isThrownBy(() -> REFERENCE.isGreaterThan(null));
        assertThatNullPointerException().isThrownBy(() -> REFERENCE.isLessThan(null));
        assertThatNullPointerException().isThrownBy(() -> REFERENCE.subtract(null));
    }
}
