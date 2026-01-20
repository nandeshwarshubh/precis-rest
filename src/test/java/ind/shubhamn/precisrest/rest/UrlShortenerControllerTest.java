package ind.shubhamn.precisrest.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ind.shubhamn.precisrest.dto.GetLongUrlRequestDTO;
import ind.shubhamn.precisrest.dto.ShortenUrlRequestDTO;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import ind.shubhamn.precisrest.service.UrlShortenerService;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UrlShortenerControllerTest {

    @Autowired private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @MockitoBean private UrlShortenerService urlShortenerService;

    @Test
    public void createShortenedUrlTest() throws Exception {
        ShortenUrlRequestDTO request = new ShortenUrlRequestDTO();
        request.setLongUrl("http://www.google.com");

        ShortenedUrl entity = new ShortenedUrl();
        entity.setShortUrl("GRNHv-Vd");
        entity.setLongUrl("http://www.google.com");

        when(urlShortenerService.shortenUrl(any(), isNull())).thenReturn(entity);
        String url = "http://localhost:8080/app/rest/shorten";
        String bodyJson = new ObjectMapper().writeValueAsString(request);
        MvcResult result =
                mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();
        verify(urlShortenerService, times(1)).shortenUrl(any(), isNull());
    }

    @Test
    public void createShortenedUrlWithExceptionTest() throws Exception {
        ShortenUrlRequestDTO request = new ShortenUrlRequestDTO();
        request.setLongUrl("http://www.google.com"); // Set a valid URL to pass validation
        when(urlShortenerService.shortenUrl(any(), isNull())).thenThrow(new RuntimeException());
        String url = "http://localhost:8080/app/rest/shorten";
        String bodyJson = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).shortenUrl(any(), isNull());
    }

    @Test
    public void getLongUrlTest() throws Exception {
        GetLongUrlRequestDTO request = new GetLongUrlRequestDTO();
        request.setShortUrl("GRNHv-Vd");

        ShortenedUrl entity = new ShortenedUrl();
        entity.setShortUrl("GRNHv-Vd");
        entity.setLongUrl("http://www.google.com");

        when(urlShortenerService.getLongUrl(any())).thenReturn(entity);
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(request);
        MvcResult result =
                mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }

    @Test
    public void getLongUrlWithExceptionTest() throws Exception {
        GetLongUrlRequestDTO request = new GetLongUrlRequestDTO();
        request.setShortUrl("GRNHv-Vd");
        when(urlShortenerService.getLongUrl(any())).thenThrow(new RuntimeException());
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }

    @Test
    public void getLongUrlWithNoSuchElementExceptionTest() throws Exception {
        GetLongUrlRequestDTO request = new GetLongUrlRequestDTO();
        request.setShortUrl("GRNHv-Vd");
        when(urlShortenerService.getLongUrl(any())).thenThrow(new NoSuchElementException());
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(request);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }
}
