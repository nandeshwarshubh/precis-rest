package ind.shubhamn.precisrest.model;

import ind.shubhamn.precisrest.validation.UrlValidator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "url_shorten",
        schema = "precis",
        indexes = {@Index(name = "idx_long_url", columnList = "long_url")})
public class ShortenedUrl {

    @Id
    @Column(name = "short_url", length = 8, nullable = false)
    private String shortUrl;

    @NotBlank(message = "URL cannot be empty")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    @UrlValidator
    @Column(name = "long_url", length = 2048, nullable = false)
    private String longUrl;

    @Transient
    @Size(min = 3, max = 8, message = "Custom alias must be between 3 and 8 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]*$",
            message = "Custom alias can only contain letters, numbers, hyphens, and underscores")
    private String customAlias;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public boolean hasCustomAlias() {
        return customAlias != null && !customAlias.trim().isEmpty();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
