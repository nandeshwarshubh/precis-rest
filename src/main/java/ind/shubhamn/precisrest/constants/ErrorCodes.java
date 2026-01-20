package ind.shubhamn.precisrest.constants;

/**
 * Centralized error codes used throughout the application. These codes are returned in error
 * responses to help clients identify the type of error.
 */
public final class ErrorCodes {

    // Prevent instantiation
    private ErrorCodes() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    /** Error code when a requested resource is not found */
    public static final String NOT_FOUND = "NOT_FOUND";

    /** Error code when a custom alias already exists in the database */
    public static final String ALIAS_ALREADY_EXISTS = "ALIAS_ALREADY_EXISTS";

    /** Error code for validation failures (e.g., invalid input) */
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    /** Error code for unexpected internal server errors */
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}
