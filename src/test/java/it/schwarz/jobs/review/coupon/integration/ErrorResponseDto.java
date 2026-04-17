package it.schwarz.jobs.review.coupon.integration;

import java.util.List;

public record ErrorResponseDto(String message, List<?> errors) {
}
