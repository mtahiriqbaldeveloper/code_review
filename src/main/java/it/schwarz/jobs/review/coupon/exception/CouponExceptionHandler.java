package it.schwarz.jobs.review.coupon.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class CouponExceptionHandler {

    record ErrorResponse(String message, List<FieldError> errors) { }

    record FieldError(String field, Object rejectedValue, String message) {
        static FieldError from(org.springframework.validation.FieldError e) {
            return new FieldError(e.getField(), e.getRejectedValue(),
                    e.getDefaultMessage() != null ? e.getDefaultMessage() : "invalid value");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors().stream().map(FieldError::from).toList();
        return ResponseEntity.badRequest().body(new ErrorResponse("Validation failed", errors));
    }

    @ExceptionHandler(CouponCodeNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(CouponCodeNotFoundException ex) {
        return toErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CouponAlreadyExistsException.class, DataIntegrityViolationException.class})
    ResponseEntity<ErrorResponse> handleConflict(Exception ex) {
        return toErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({CouponNotValidException.class, BasketValueTooLowException.class})
    ResponseEntity<ErrorResponse> handleUnprocessable(Exception ex) {
        return toErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return toErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnhandled(Exception ex) {
        return toErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toErrorResponse(Exception ex, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(ex.getMessage(), List.of()));
    }
}
