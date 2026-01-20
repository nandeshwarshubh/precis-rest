package ind.shubhamn.precisrest.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for the response when retrieving a long URL. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetLongUrlResponseDTO {

    private String shortUrl;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public GetLongUrlResponseDTO(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }
}
