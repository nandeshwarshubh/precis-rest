package ind.shubhamn.precisrest.dto;

import ind.shubhamn.precisrest.validation.UrlValidator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for URL shortening requests. Contains the long URL to be shortened and an optional custom
 * alias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrlRequestDTO {

    @NotBlank(message = "URL cannot be empty")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    @UrlValidator
    private String longUrl;

    @Size(min = 3, max = 8, message = "Custom alias must be between 3 and 8 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_-]*$",
            message = "Custom alias can only contain letters, numbers, hyphens, and underscores")
    private String customAlias;

    public ShortenUrlRequestDTO(String longUrl) {
        this.longUrl = longUrl;
    }

    public boolean hasCustomAlias() {
        return customAlias != null && !customAlias.trim().isEmpty();
    }
}
