package ind.shubhamn.precisrest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for retrieving the original long URL from a short URL. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetLongUrlRequestDTO {

    @NotBlank(message = "Short URL cannot be empty")
    @Size(max = 8, message = "Short URL cannot exceed 8 characters")
    private String shortUrl;
}
