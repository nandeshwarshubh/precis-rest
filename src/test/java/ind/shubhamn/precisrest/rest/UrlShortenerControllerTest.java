package ind.shubhamn.precisrest.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ind.shubhamn.precisrest.model.ShortenedUrl;
import ind.shubhamn.precisrest.service.UrlShortenerService;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Assertions;
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
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setLongUrl("http://www.google.com");
        when(urlShortenerService.shortenUrl(any())).thenReturn("GRNHv-Vd");
        String url = "http://localhost:8080/app/rest/shorten";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        MvcResult result =
                mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();
        verify(urlShortenerService, times(1)).shortenUrl(any());
        shortenedUrl.setShortUrl("GRNHv-Vd");
        String expectedResult = new ObjectMapper().writeValueAsString(shortenedUrl);
        String testResult = result.getResponse().getContentAsString();
        Assertions.assertEquals(expectedResult, testResult);
    }

    @Test
    public void createShortenedUrlWithExceptionTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setLongUrl("http://www.google.com"); // Set a valid URL to pass validation
        when(urlShortenerService.shortenUrl(any())).thenThrow(new RuntimeException());
        String url = "http://localhost:8080/app/rest/shorten";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).shortenUrl(any());
    }

    @Test
    public void getLongUrlTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setShortUrl("GRNHv-Vd");
        when(urlShortenerService.getLongUrl(any())).thenReturn("http://www.google.com");
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        MvcResult result =
                mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();
        verify(urlShortenerService, times(1)).getLongUrl(any());
        shortenedUrl.setLongUrl("http://www.google.com");
        String expectedResult = new ObjectMapper().writeValueAsString(shortenedUrl);
        String testResult = result.getResponse().getContentAsString();
        Assertions.assertEquals(expectedResult, testResult);
    }

    @Test
    public void getLongUrlWithExceptionTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        when(urlShortenerService.getLongUrl(any())).thenThrow(new RuntimeException());
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }

    @Test
    public void getLongUrlWithNoSuchElementExceptionTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        when(urlShortenerService.getLongUrl(any())).thenThrow(new NoSuchElementException());
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(bodyJson))
                .andDo(print())
                .andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }
}
