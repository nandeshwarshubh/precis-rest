package ind.shubhamn.precisrest.rest;

import ind.shubhamn.precisrest.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.NoSuchElementException;

@RestController
@Scope(WebApplicationContext.SCOPE_REQUEST)
@RequestMapping("app/rest")
public class UrlShortenerController {

    private static final String errorCode = "NOT_FOUND";

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping(value = "shorten")
    public ResponseEntity<String> createShortenedUrl(@RequestBody String longUrl) {
        try {
            return ResponseEntityHelper.successResponseEntity(urlShortenerService.shortenUrl(longUrl));
        } catch (Exception e) {
            return ResponseEntityHelper.failureResponseEntity(e, null);
        }
    }

    @PostMapping(value = "getLong")
    public ResponseEntity<String> getLongUrl(@RequestBody String shortUrl) {
        try {
            return ResponseEntityHelper.successResponseEntity(urlShortenerService.getLongUrl(shortUrl));
        } catch (NoSuchElementException e) {
            return ResponseEntityHelper.failureResponseEntity(e, errorCode);
        } catch (Exception e) {
            return ResponseEntityHelper.failureResponseEntity(e, null);
        }
    }
}
