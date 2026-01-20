package ind.shubhamn.precisrest.rest;

import ind.shubhamn.precisrest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Helper class for creating standardized ResponseEntity objects for both success and error
 * responses. All controller methods and exception handlers should use this class to ensure
 * consistent response formatting.
 */
public class ResponseEntityHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResponseEntityHelper.class);

    /**
     * Creates a success ResponseEntity with HTTP 200 OK status.
     *
     * @param body The response body
     * @param <T> The type of the response body
     * @return ResponseEntity with the body and 200 OK status
     */
    public static <T> ResponseEntity<T> successResponseEntity(T body) {
        logger.trace(
                "Creating success response entity with HTTP 200 OK, bodyType={}",
                body != null ? body.getClass().getSimpleName() : "null");

        logger.debug("Success response created: status=200, body={}", body);

        logger.info("Returning successful response with HTTP 200 OK");

        return ResponseEntity.ok(body);
    }

    /**
     * Creates a success ResponseEntity with a custom HTTP status.
     *
     * @param body The response body
     * @param status The HTTP status code
     * @param <T> The type of the response body
     * @return ResponseEntity with the body and specified status
     */
    public static <T> ResponseEntity<T> successResponseEntity(T body, HttpStatus status) {
        logger.trace(
                "Creating success response entity with custom status, status={}, bodyType={}",
                status,
                body != null ? body.getClass().getSimpleName() : "null");

        logger.debug("Success response created: status={}, body={}", status.value(), body);

        logger.info("Returning successful response with HTTP {}", status.value());

        return ResponseEntity.status(status).body(body);
    }

    /**
     * Creates an error ResponseEntity with ErrorResponse body and HTTP 500 Internal Server Error
     * status.
     *
     * @param exception The exception that occurred
     * @param errorCode The error code to include in the response
     * @return ResponseEntity with ErrorResponse body and 500 status
     */
    public static ResponseEntity<ErrorResponse> failureResponseEntity(
            Exception exception, String errorCode) {
        logger.trace(
                "Delegating to failureResponseEntity with default status 500, errorCode={}",
                errorCode);
        return failureResponseEntity(exception, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates an error ResponseEntity with ErrorResponse body and custom HTTP status.
     *
     * @param exception The exception that occurred
     * @param errorCode The error code to include in the response
     * @param status The HTTP status code
     * @return ResponseEntity with ErrorResponse body and specified status
     */
    public static ResponseEntity<ErrorResponse> failureResponseEntity(
            Exception exception, String errorCode, HttpStatus status) {

        logger.trace(
                "Creating failure response entity: errorCode={}, status={}, exceptionType={}",
                errorCode,
                status,
                exception.getClass().getSimpleName(),
                exception);

        logger.debug(
                "Error details: errorCode={}, message={}, status={}",
                errorCode,
                exception.getMessage(),
                status.value(),
                exception);

        if (status.is4xxClientError()) {
            logger.warn(
                    "Client error occurred: errorCode={}, status={}, message={}",
                    errorCode,
                    status.value(),
                    exception.getMessage());
        }

        if (status.is5xxServerError()) {
            logger.error(
                    "Server error occurred: errorCode={}, status={}, message={}",
                    errorCode,
                    status.value(),
                    exception.getMessage(),
                    exception);
        }

        ErrorResponse errorResponse =
                new ErrorResponse(errorCode, exception.getMessage(), status.value());

        logger.info("Returning error response: errorCode={}, status={}", errorCode, status.value());

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Creates an error ResponseEntity with ErrorResponse body, custom message, and custom HTTP
     * status.
     *
     * @param errorCode The error code to include in the response
     * @param message The error message
     * @param status The HTTP status code
     * @return ResponseEntity with ErrorResponse body and specified status
     */
    public static ResponseEntity<ErrorResponse> failureResponseEntity(
            String errorCode, String message, HttpStatus status) {

        logger.trace(
                "Creating failure response entity with custom message: errorCode={}, status={},"
                        + " message={}",
                errorCode,
                status,
                message);

        logger.debug(
                "Error details: errorCode={}, message={}, status={}",
                errorCode,
                message,
                status.value());

        if (status.is4xxClientError()) {
            logger.warn(
                    "Client error occurred: errorCode={}, status={}, message={}",
                    errorCode,
                    status.value(),
                    message);
        }

        if (status.is5xxServerError()) {
            logger.error(
                    "Server error occurred: errorCode={}, status={}, message={}",
                    errorCode,
                    status.value(),
                    message);
        }

        ErrorResponse errorResponse = new ErrorResponse(errorCode, message, status.value());

        logger.info("Returning error response: errorCode={}, status={}", errorCode, status.value());

        return ResponseEntity.status(status).body(errorResponse);
    }
}
