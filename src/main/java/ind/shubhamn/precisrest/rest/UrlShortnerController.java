package ind.shubhamn.precisrest.rest;

import ind.shubhamn.precisrest.service.UrlShortnerService;
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
public class UrlShortnerController {

    @Autowired
    private UrlShortnerService urlShortnerService;

    @PostMapping(value = "shorten")
    public ResponseEntity<String> createShortenedUrl(@RequestBody String longUrl) {
        try {
            return ResponseEntityHelper.successResponseEntity(urlShortnerService.shortenUrl(longUrl));
        } catch (Exception e) {
            return ResponseEntityHelper.failureResponseEntity(e);
        }
    }
}
