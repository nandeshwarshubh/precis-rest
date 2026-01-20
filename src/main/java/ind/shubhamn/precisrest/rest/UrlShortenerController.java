package ind.shubhamn.precisrest.rest;

import ind.shubhamn.precisrest.constants.ErrorCodes;
import ind.shubhamn.precisrest.dto.GetLongUrlRequestDTO;
import ind.shubhamn.precisrest.dto.GetLongUrlResponseDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlRequestDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlResponseDTO;
import ind.shubhamn.precisrest.mapper.UrlMapper;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import ind.shubhamn.precisrest.service.UrlShortenerService;
import jakarta.validation.Valid;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@RestController
@Scope(WebApplicationContext.SCOPE_REQUEST)
@RequestMapping("app/rest")
public class UrlShortenerController {

    @Autowired private UrlShortenerService urlShortenerService;

    @Autowired private UrlMapper urlMapper;

    /**
     * Creates a shortened URL with optional custom alias
     *
     * @param requestDto The request containing longUrl and optional customAlias
     * @return ResponseEntity with the shortened URL details
     */
    @PostMapping(value = "shorten")
    public ResponseEntity<ShortenUrlResponseDTO> createShortenedUrl(
            @Valid @RequestBody ShortenUrlRequestDTO requestDto) throws Exception {

        ShortenedUrl entity =
                urlShortenerService.shortenUrl(
                        requestDto.getLongUrl(), requestDto.getCustomAlias());

        ShortenUrlResponseDTO responseDto = urlMapper.toShortenUrlResponseDto(entity);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping(value = "long")
    public ResponseEntity<GetLongUrlResponseDTO> getLongUrl(
            @Valid @RequestBody GetLongUrlRequestDTO requestDto) {
        try {
            ShortenedUrl entity = urlShortenerService.getLongUrl(requestDto.getShortUrl());
            GetLongUrlResponseDTO responseDto = urlMapper.toGetLongUrlResponseDto(entity);
            return ResponseEntity.ok(responseDto);
        } catch (NoSuchElementException e) {
            return ResponseEntityHelper.failureResponseEntity(e, ErrorCodes.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntityHelper.failureResponseEntity(e, null);
        }
    }
}
