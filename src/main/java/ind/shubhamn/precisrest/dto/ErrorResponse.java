package ind.shubhamn.precisrest.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ErrorResponse {

    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
