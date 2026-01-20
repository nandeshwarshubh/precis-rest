package ind.shubhamn.precisrest.mapper;

import ind.shubhamn.precisrest.dto.GetLongUrlResponseDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlRequestDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlResponseDTO;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper for converting between ShortenedUrl entity and DTOs. The implementation is
 * automatically generated at compile time.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UrlMapper {

    /**
     * Maps ShortenUrlRequestDTO to ShortenedUrl entity. Used when creating a new shortened URL.
     *
     * @param requestDto the request DTO
     * @return the entity
     */
    @Mapping(target = "shortUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    ShortenedUrl toEntity(ShortenUrlRequestDTO requestDto);

    /**
     * Maps ShortenedUrl entity to ShortenUrlResponseDTO. Used when returning the shortened URL to
     * the client.
     *
     * @param entity the entity
     * @return the response DTO
     */
    @Mapping(target = "shortUrl", source = "shortUrl")
    @Mapping(target = "longUrl", source = "longUrl")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "expiresAt", source = "expiresAt")
    ShortenUrlResponseDTO toShortenUrlResponseDto(ShortenedUrl entity);

    /**
     * Maps ShortenedUrl entity to GetLongUrlResponseDTO. Used when retrieving the original URL.
     *
     * @param entity the entity
     * @return the response DTO
     */
    @Mapping(target = "shortUrl", source = "shortUrl")
    @Mapping(target = "longUrl", source = "longUrl")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "expiresAt", source = "expiresAt")
    GetLongUrlResponseDTO toGetLongUrlResponseDto(ShortenedUrl entity);
}
