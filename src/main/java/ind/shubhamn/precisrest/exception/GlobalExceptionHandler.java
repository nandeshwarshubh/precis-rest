package ind.shubhamn.precisrest.exception;

import ind.shubhamn.precisrest.constants.ErrorCodes;
import ind.shubhamn.precisrest.dto.ErrorResponse;
import ind.shubhamn.precisrest.rest.ResponseEntityHelper;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application. All exceptions thrown by controllers are caught
 * here and converted to standardized error responses using ResponseEntityHelper.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ShortUrlAlreadyExistsException when a custom alias is already in use.
     *
     * @param ex The exception
     * @return ResponseEntity with ErrorResponse and HTTP 409 Conflict status
     */
    @ExceptionHandler(ShortUrlAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleShortUrlAlreadyExists(
            ShortUrlAlreadyExistsException ex) {

        logger.trace("Handling ShortUrlAlreadyExistsException: message={}", ex.getMessage(), ex);

        logger.debug(
                "Custom alias conflict detected: errorCode={}, message={}",
                ErrorCodes.ALIAS_ALREADY_EXISTS,
                ex.getMessage());

        logger.info("Short URL alias already exists, returning 409 Conflict: {}", ex.getMessage());

        logger.warn(
                "Client attempted to use existing alias: errorCode={}",
                ErrorCodes.ALIAS_ALREADY_EXISTS);

        return ResponseEntityHelper.failureResponseEntity(
                ex, ErrorCodes.ALIAS_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    /**
     * Handles validation exceptions from @Valid annotations.
     *
     * @param ex The validation exception
     * @return ResponseEntity with ErrorResponse and HTTP 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        logger.trace(
                "Handling MethodArgumentNotValidException: fieldErrorCount={}",
                ex.getBindingResult().getFieldErrorCount(),
                ex);

        StringBuilder message = new StringBuilder("Validation failed: ");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            message.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");

            logger.debug(
                    "Validation error: field={}, rejectedValue={}, message={}",
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage());
        }

        logger.info(
                "Validation failed for request, returning 400 Bad Request: {}", message.toString());

        logger.warn(
                "Client sent invalid request data: errorCode={}, errors={}",
                ErrorCodes.VALIDATION_ERROR,
                ex.getBindingResult().getFieldErrorCount());

        return ResponseEntityHelper.failureResponseEntity(
                ErrorCodes.VALIDATION_ERROR, message.toString(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles NoSuchElementException when a requested resource is not found.
     *
     * @param ex The exception
     * @return ResponseEntity with ErrorResponse and HTTP 404 Not Found status
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {

        logger.trace("Handling NoSuchElementException: message={}", ex.getMessage(), ex);

        logger.debug(
                "Resource not found: errorCode={}, message={}",
                ErrorCodes.NOT_FOUND,
                ex.getMessage());

        logger.info("Requested resource not found, returning 404 Not Found");

        logger.warn("Client requested non-existent resource: errorCode={}", ErrorCodes.NOT_FOUND);

        return ResponseEntityHelper.failureResponseEntity(
                ex, ErrorCodes.NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex The exception
     * @return ResponseEntity with ErrorResponse and HTTP 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        logger.trace(
                "Handling generic Exception: type={}, message={}",
                ex.getClass().getName(),
                ex.getMessage(),
                ex);

        logger.debug(
                "Unexpected exception caught: exceptionType={}, message={}",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex);

        logger.error(
                "Unhandled exception occurred: errorCode={}, exceptionType={}, message={}",
                ErrorCodes.INTERNAL_SERVER_ERROR,
                ex.getClass().getName(),
                ex.getMessage(),
                ex);

        String message = "An unexpected error occurred: " + ex.getMessage();

        logger.info("Returning 500 Internal Server Error for unhandled exception");

        return ResponseEntityHelper.failureResponseEntity(
                ErrorCodes.INTERNAL_SERVER_ERROR, message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
