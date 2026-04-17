package it.schwarz.jobs.review.coupon.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * HTTP status mapping for coupon API errors
 * <ul>
 *   <li>{@link CouponCodeNotFoundException} → 404 Not Found</li>
 *   <li>{@link CouponAlreadyExistsException}, {@link DataIntegrityViolationException} → 409 Conflict</li>
 *   <li>{@link CouponNotValidException}, {@link BasketValueTooLowException} → 422 Unprocessable Entity</li>
 *   <li>{@link BusinessException} (other) → 400 Bad Request</li>
 * </ul>
 */
@ControllerAdvice
public class CouponExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CouponCodeNotFoundException.class)
    protected ResponseEntity<Object> handleCouponCodeNotFound(CouponCodeNotFoundException ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(CouponAlreadyExistsException.class)
    protected ResponseEntity<Object> handleCouponAlreadyExists(CouponAlreadyExistsException ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(CouponNotValidException.class)
    protected ResponseEntity<Object> handleCouponNotValid(CouponNotValidException ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(BasketValueTooLowException.class)
    protected ResponseEntity<Object> handleBasketValueTooLow(BasketValueTooLowException ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    /** Fallback for {@link BusinessException} thrown without a more specific subtype */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleUnhandled(Exception ex, WebRequest request) {
        return toErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> toErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setDetail(ex.getMessage());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }
}
