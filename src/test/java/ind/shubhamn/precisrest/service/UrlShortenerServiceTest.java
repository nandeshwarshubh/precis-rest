package ind.shubhamn.precisrest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ind.shubhamn.precisrest.dao.UrlShortenerDAO;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UrlShortenerServiceTest {

    @InjectMocks private UrlShortenerService urlShortenerService;

    @Mock private UrlShortenerDAO urlShortenerDAO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shortenUrlTest() throws Exception {
        // Arrange
        String longUrl = "http://www.google.com";
        when(urlShortenerDAO.save(any(ShortenedUrl.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ShortenedUrl result = urlShortenerService.shortenUrl(longUrl, null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getShortUrl());
        assertEquals(longUrl, result.getLongUrl());
        assertEquals(8, result.getShortUrl().length());
    }

    @Test
    public void getLongUrlTest() throws Exception {
        // Arrange
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setShortUrl("GRNHv-Vd");
        shortenedUrl.setLongUrl("http://www.google.com");

        when(urlShortenerDAO.findByShortUrl("GRNHv-Vd")).thenReturn(Optional.of(shortenedUrl));

        // Act
        ShortenedUrl result = urlShortenerService.getLongUrl("GRNHv-Vd");

        // Assert
        assertNotNull(result);
        assertEquals("GRNHv-Vd", result.getShortUrl());
        assertEquals("http://www.google.com", result.getLongUrl());
    }
}
