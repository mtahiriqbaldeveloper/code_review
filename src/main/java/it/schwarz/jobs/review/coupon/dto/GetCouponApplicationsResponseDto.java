package it.schwarz.jobs.review.coupon.dto;

import java.time.Instant;
import java.util.List;

public record GetCouponApplicationsResponseDto(
        String couponCode,
        List<Instant> applicationTimestamps
) {
    public static GetCouponApplicationsResponseDto of(CouponApplications couponApplications) {
        return new GetCouponApplicationsResponseDto(
                couponApplications.couponCode(),
                couponApplications.applicationTimestamps());
    }
}
