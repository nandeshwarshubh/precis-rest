package ind.shubhamn.precisrest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity representing a shortened URL mapping. Maps to the url_shorten table in the precis
 * schema.
 */
@Entity
@Table(
        name = "url_shorten",
        schema = "precis",
        indexes = {@Index(name = "idx_long_url", columnList = "long_url")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenedUrl {

    @Id
    @Column(name = "short_url", length = 8, nullable = false)
    private String shortUrl;

    @Column(name = "long_url", length = 2048, nullable = false)
    private String longUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    public ShortenedUrl(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
