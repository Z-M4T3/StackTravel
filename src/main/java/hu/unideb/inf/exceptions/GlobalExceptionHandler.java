package hu.unideb.inf.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException ex) {
        return new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUsernameAlreadyTaken(UsernameAlreadyTakenException ex) {
        return new ErrorResponse("USERNAME_ALREADY_TAKEN", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyTaken(EmailAlreadyTakenException ex) {
        return new ErrorResponse("EMAIL_ALREADY_TAKEN", ex.getMessage());
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleIncorrectPassword(IncorrectPasswordException ex) {
        return new ErrorResponse("INCORRECT_PASSWORD", ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidToken(InvalidPasswordResetTokenException ex) {
        return new ErrorResponse("INVALID_PASSWORD_RESET_TOKEN", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidArgument(IllegalArgumentException ex) {
        return new ErrorResponse("INVALID_REQUEST", "Invalid request data.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflict(DataIntegrityViolationException ex) {
        return new ErrorResponse("DATA_CONFLICT", "The operation conflicts with existing data.");
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse response;
        if (ex instanceof MethodArgumentNotValidException validation) {
            String message = validation.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .sorted().collect(Collectors.joining("; "));
            response = new ErrorResponse("VALIDATION_ERROR", message);
        } else if (ex instanceof HttpMessageNotReadableException) {
            response = new ErrorResponse("INVALID_JSON",
                    "The request body is missing or contains invalid JSON.");
        } else {
            HttpStatus httpStatus = HttpStatus.resolve(status.value());
            String code = httpStatus == null ? "HTTP_ERROR" : httpStatus.name();
            String message = httpStatus == null ? "Request failed." : httpStatus.getReasonPhrase();
            if (status.is5xxServerError()) {
                log.error("HTTP request failed with status {}", status.value(), ex);
                code = "INTERNAL_ERROR";
                message = "Unexpected error occurred";
            } else if (ex instanceof org.springframework.web.ErrorResponse error
                    && error.getBody().getDetail() != null) {
                message = error.getBody().getDetail();
            }
            response = new ErrorResponse(code, message);
        }
        return super.handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex) {
        log.error("Unexpected error while processing an HTTP request", ex);
        return new ErrorResponse("INTERNAL_ERROR", "Unexpected error occurred");
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
    }
}
