package ind.shubhamn.precisrest.rest;

import ind.shubhamn.precisrest.dto.GetLongUrlRequestDTO;
import ind.shubhamn.precisrest.dto.GetLongUrlResponseDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlRequestDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlResponseDTO;
import ind.shubhamn.precisrest.mapper.UrlMapper;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import ind.shubhamn.precisrest.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

/**
 * REST controller for URL shortening operations. All responses are created using
 * ResponseEntityHelper for consistency. Exceptions are handled by GlobalExceptionHandler.
 */
@RestController
@Scope(WebApplicationContext.SCOPE_REQUEST)
@RequestMapping("app/rest")
public class UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

    @Autowired private UrlShortenerService urlShortenerService;

    @Autowired private UrlMapper urlMapper;

    /**
     * Creates a shortened URL with optional custom alias.
     *
     * @param requestDto The request containing longUrl and optional customAlias
     * @return ResponseEntity with the shortened URL details
     * @throws Exception if URL shortening fails
     */
    @PostMapping(value = "shorten")
    public ResponseEntity<ShortenUrlResponseDTO> createShortenedUrl(
            @Valid @RequestBody ShortenUrlRequestDTO requestDto) throws Exception {

        logger.trace(
                "Received shorten URL request: longUrl={}, customAlias={}",
                requestDto.getLongUrl(),
                requestDto.getCustomAlias());

        logger.debug("Processing URL shortening request for: {}", requestDto.getLongUrl());

        logger.info("Creating shortened URL for: {}", requestDto.getLongUrl());

        ShortenedUrl entity =
                urlShortenerService.shortenUrl(
                        requestDto.getLongUrl(), requestDto.getCustomAlias());

        logger.debug("URL shortened successfully: shortUrl={}", entity.getShortUrl());

        logger.info("Successfully created short URL: {}", entity.getShortUrl());

        ShortenUrlResponseDTO responseDto = urlMapper.toShortenUrlResponseDto(entity);

        logger.trace("Returning response: {}", responseDto);

        return ResponseEntityHelper.successResponseEntity(responseDto);
    }

    /**
     * Retrieves the original long URL from a shortened URL.
     *
     * @param requestDto The request containing the short URL
     * @return ResponseEntity with the long URL details
     */
    @PostMapping(value = "long")
    public ResponseEntity<GetLongUrlResponseDTO> getLongUrl(
            @Valid @RequestBody GetLongUrlRequestDTO requestDto) {

        logger.trace("Received get long URL request: shortUrl={}", requestDto.getShortUrl());

        logger.debug("Looking up long URL for short URL: {}", requestDto.getShortUrl());

        logger.info("Retrieving long URL for: {}", requestDto.getShortUrl());

        ShortenedUrl entity = urlShortenerService.getLongUrl(requestDto.getShortUrl());

        logger.debug("Found long URL: {}", entity.getLongUrl());

        logger.info("Successfully retrieved long URL for: {}", requestDto.getShortUrl());

        GetLongUrlResponseDTO responseDto = urlMapper.toGetLongUrlResponseDto(entity);

        logger.trace("Returning response: {}", responseDto);

        return ResponseEntityHelper.successResponseEntity(responseDto);
    }
}
