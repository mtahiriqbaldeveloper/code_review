package it.schwarz.jobs.review.coupon.dto;

import java.time.Instant;
import java.util.List;

public record CouponApplications(String couponCode, List<Instant> applicationTimestamps) {
}
