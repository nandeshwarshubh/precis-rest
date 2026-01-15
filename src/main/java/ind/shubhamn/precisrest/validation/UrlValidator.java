package ind.shubhamn.precisrest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/** Custom validation annotation for URL validation */
@Documented
@Constraint(validatedBy = UrlValidatorImpl.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlValidator {
    String message() default "Invalid URL format or malicious URL detected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
