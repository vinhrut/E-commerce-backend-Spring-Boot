package CV.ecommerce.exception;

import CV.ecommerce.dto.response.APIResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(AppException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<String> handleAppException(AppException ex) {
        return new APIResponse<>(
                ex.getCode(),
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<String> handleValidation(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new APIResponse<>(
                ErrorCode.VALIDATION_FAILED.getCode(),
                errors,
                null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public APIResponse<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return new APIResponse<>(
                ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                "Phương thức không được hỗ trợ: " + e.getMethod(),
                null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<String> handleInvalidJson(HttpMessageNotReadableException e) {
        return new APIResponse<>(
                ErrorCode.INVALID_JSON.getCode(),
                ErrorCode.INVALID_JSON.getMessage(),
                null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<String> handleMissingParam(MissingServletRequestParameterException e) {
        return new APIResponse<>(
                ErrorCode.PARAM_MISSING.getCode(),
                "Thiếu tham số: " + e.getParameterName(),
                null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<String> handleConstraintViolation(ConstraintViolationException e) {
        return new APIResponse<>(
                ErrorCode.CONSTRAINT_VIOLATION.getCode(),
                "Vi phạm ràng buộc dữ liệu: " + e.getMessage(),
                null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public APIResponse<String> handleAccessDenied(AccessDeniedException e) {
        return new APIResponse<>(
                ErrorCode.ACCESS_DENIED.getCode(),
                ErrorCode.ACCESS_DENIED.getMessage(),
                null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public APIResponse<String> handleResponseStatusException(ResponseStatusException e) {
        return new APIResponse<>(
                e.getStatusCode().value(),
                e.getReason() != null ? e.getReason() : "Lỗi không xác định",
                null);
    }
}
