package ind.shubhamn.precisrest.rest;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseEntityHelper {

    private static final Logger logger = LoggerFactory.getLogger(ResponseEntityHelper.class);

    public static <T> ResponseEntity successResponseEntity(T t) {
        return new ResponseEntity(t, HttpStatus.OK);
    }

    public static <T> ResponseEntity failureResponseEntity(Exception e, String errorCode) {
        String message = e.getMessage();
        String timeString = String.valueOf(System.currentTimeMillis());
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("id", timeString);
        responseMap.put("message", message);
        responseMap.put("errorCode", errorCode);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseMap);
    }
}
