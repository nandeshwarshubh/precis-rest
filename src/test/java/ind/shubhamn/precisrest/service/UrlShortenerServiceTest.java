package ind.shubhamn.precisrest.service;

import ind.shubhamn.precisrest.dao.UrlShortenerDAO;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UrlShortenerServiceTest {

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Mock
    private UrlShortenerDAO urlShortenerDAO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shortenUrlTest() throws Exception {
        ShortenedUrl shortenedUrl = Mockito.mock(ShortenedUrl.class);
        when(shortenedUrl.getLongUrl()).thenReturn("http://www.google.com");
        when(shortenedUrl.getShortUrl()).thenReturn("GRNHv-Vd");
        when(urlShortenerDAO.save(shortenedUrl)).thenReturn(shortenedUrl);
        urlShortenerService.shortenUrl("http://www.google.com");
    }

    @Test
    public void getLongUrlTest() throws Exception {
        ShortenedUrl shortenedUrl = Mockito.mock(ShortenedUrl.class);
        Optional<ShortenedUrl> shortenedUrlOptional = Optional.of((ShortenedUrl) shortenedUrl);
        when(urlShortenerDAO.findByShortUrl("GRNHv-Vd")).thenReturn(shortenedUrlOptional);
        urlShortenerService.getLongUrl("GRNHv-Vd");
    }
}
