package it.schwarz.jobs.review.coupon.api.exception;

public record ErrorResponseDto(
        String type,
        String title,
        int status,
        String detail,
        String instance
) {
}
