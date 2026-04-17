package it.schwarz.jobs.review.coupon.exception;

public class CouponAlreadyExistsException extends BusinessException {
    public CouponAlreadyExistsException(String detail) {
        super(detail);
    }
}
