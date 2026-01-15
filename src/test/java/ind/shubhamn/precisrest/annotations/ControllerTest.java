package ind.shubhamn.precisrest.annotations;

import java.lang.annotation.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@WebMvcTest
@ExtendWith(SpringExtension.class)
// @ActiveProfiles(profiles = "unittest")
public @interface ControllerTest {}
