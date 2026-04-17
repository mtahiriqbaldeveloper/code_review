package it.schwarz.jobs.review.coupon.testdata;

import it.schwarz.jobs.review.coupon.common.AmountOfMoney;
import it.schwarz.jobs.review.coupon.dto.ApplyCouponRequestDto;
import it.schwarz.jobs.review.coupon.dto.BasketDto;
import it.schwarz.jobs.review.coupon.dto.CouponPayload;
import it.schwarz.jobs.review.coupon.dto.CreateCouponRequestDto;

import java.math.BigDecimal;

public final class TestData {

    public static final String SEEDED_COUPON_CODE = "TEST_05_50";

    public static CouponPayload coupon12For20() {
        return new CouponPayload("CODE_12_20", AmountOfMoney.of("12.00"), AmountOfMoney.of("20.00"), "12 for 20");
    }

    public static CreateCouponRequestDto validCouponRequest() {
        return new CreateCouponRequestDto("CODE_12_20", new BigDecimal("12.00"), new BigDecimal("20.00"), "12 for 20");
    }

    public static CreateCouponRequestDto invalidCouponRequest() {
        return new CreateCouponRequestDto("CODE_12_20", new BigDecimal("-12.00"), new BigDecimal("20.00"), "12 for 20");
    }

    public static ApplyCouponRequestDto validApplyRequest() {
        return new ApplyCouponRequestDto(new BasketDto(new BigDecimal("60.00")), SEEDED_COUPON_CODE);
    }

    public static ApplyCouponRequestDto unknownCodeApplyRequest() {
        return new ApplyCouponRequestDto(new BasketDto(new BigDecimal("60.00")), "UNKNOWN_CODE");
    }

    private TestData() {}
}
