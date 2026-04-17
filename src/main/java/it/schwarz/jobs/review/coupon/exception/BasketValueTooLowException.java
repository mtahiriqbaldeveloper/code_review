package it.schwarz.jobs.review.coupon.exception;

public class BasketValueTooLowException extends BusinessException {
    public BasketValueTooLowException(String detail) {
        super(detail);
    }
}
