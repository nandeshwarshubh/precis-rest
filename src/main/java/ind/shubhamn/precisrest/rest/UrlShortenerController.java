package ind.shubhamn.precisrest.rest;

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

    private static final String errorCode = "NOT_FOUND";

    @Autowired private UrlShortenerService urlShortenerService;

    /**
     * Creates a shortened URL with optional custom alias
     *
     * @param shortenedUrl The request containing longUrl and optional customAlias
     * @return ResponseEntity with the shortened URL details
     */
    @PostMapping(value = "shorten")
    public ResponseEntity<ShortenedUrl> createShortenedUrl(
            @Valid @RequestBody ShortenedUrl shortenedUrl) throws Exception {

        String shortUrl =
                urlShortenerService.shortenUrl(
                        shortenedUrl.getLongUrl(), shortenedUrl.getCustomAlias());

        shortenedUrl.setShortUrl(shortUrl);
        return ResponseEntityHelper.successResponseEntity(shortenedUrl);
    }

    @PostMapping(value = "long")
    public ResponseEntity<ShortenedUrl> getLongUrl(@RequestBody ShortenedUrl shortenedUrl) {
        try {
            shortenedUrl.setLongUrl(urlShortenerService.getLongUrl(shortenedUrl.getShortUrl()));
            return ResponseEntityHelper.successResponseEntity(shortenedUrl);
        } catch (NoSuchElementException e) {
            return ResponseEntityHelper.failureResponseEntity(e, errorCode);
        } catch (Exception e) {
            return ResponseEntityHelper.failureResponseEntity(e, null);
        }
    }
}
