package ind.shubhamn.precisrest.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for URL shortening responses. Contains the shortened URL details returned to the client. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlResponseDTO {

    private String shortUrl;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public ShortenUrlResponseDTO(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }
}
