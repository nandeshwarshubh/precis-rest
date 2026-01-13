package ind.shubhamn.precisrest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/** Implementation of URL validator Validates URL format and checks for malicious patterns */
@Component
public class UrlValidatorImpl implements ConstraintValidator<UrlValidator, String> {

    // Blacklisted schemes
    private static final List<String> BLACKLISTED_SCHEMES =
            Arrays.asList("javascript", "data", "file", "vbscript");

    // Blacklisted domains (example - expand as needed)
    private static final List<String> BLACKLISTED_DOMAINS =
            Arrays.asList("localhost", "127.0.0.1", "0.0.0.0");

    @Override
    public void initialize(UrlValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            URI uri = new URI(value);

            // Ensure the URI has a scheme and host
            if (uri.getScheme() == null || uri.getHost() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "URL must have a valid scheme and host")
                        .addConstraintViolation();
                return false;
            }

            // Check for blacklisted schemes
            String scheme = uri.getScheme().toLowerCase();
            if (BLACKLISTED_SCHEMES.contains(scheme)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                                "URL scheme '" + scheme + "' is not allowed")
                        .addConstraintViolation();
                return false;
            }

            // Only allow HTTP and HTTPS
            if (!scheme.equals("http") && !scheme.equals("https")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Only HTTP and HTTPS URLs are allowed")
                        .addConstraintViolation();
                return false;
            }

            // Check for blacklisted domains (optional - can be disabled for development)
            String host = uri.getHost().toLowerCase();
            // Uncomment to enable domain blacklist
            // if (BLACKLISTED_DOMAINS.stream().anyMatch(host::contains)) {
            //     context.disableDefaultConstraintViolation();
            //     context.buildConstraintViolationWithTemplate(
            //         "URL domain is not allowed"
            //     ).addConstraintViolation();
            //     return false;
            // }

            // Check for suspicious patterns
            if (containsSuspiciousPatterns(value)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("URL contains suspicious patterns")
                        .addConstraintViolation();
                return false;
            }

            return true;

        } catch (URISyntaxException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid URL format: " + e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }

    private boolean containsSuspiciousPatterns(String url) {
        String lowerUrl = url.toLowerCase();

        // Check for common XSS patterns
        List<String> suspiciousPatterns =
                Arrays.asList(
                        "<script",
                        "javascript:",
                        "onerror=",
                        "onload=",
                        "eval(",
                        "alert(",
                        "document.cookie");

        return suspiciousPatterns.stream().anyMatch(lowerUrl::contains);
    }
}
