package it.schwarz.jobs.review.coupon.exception;

public class CouponCodeNotFoundException extends BusinessException {
    public CouponCodeNotFoundException(String detail) {
        super(detail);
    }
}
