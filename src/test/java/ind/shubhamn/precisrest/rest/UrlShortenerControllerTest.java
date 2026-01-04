package ind.shubhamn.precisrest.rest;

import tools.jackson.databind.ObjectMapper;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import ind.shubhamn.precisrest.service.UrlShortenerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShortenerService urlShortenerService;


    @Test
    public void createShortenedUrlTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setLongUrl("http://www.google.com");
        when(urlShortenerService.shortenUrl(any())).thenReturn("GRNHv-Vd");
        String url = "http://localhost:8080/app/rest/shorten";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        MvcResult result = mockMvc.perform(post(url).
                contentType(MediaType.APPLICATION_JSON).
                content(bodyJson)).andDo(print()).andExpect(status().isOk())
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
        when(urlShortenerService.shortenUrl(any())).thenThrow(new RuntimeException());
        String url = "http://localhost:8080/app/rest/shorten";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        mockMvc.perform(post(url).
                contentType(MediaType.APPLICATION_JSON).
                content(bodyJson)).andDo(print()).andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).shortenUrl(any());
    }

    @Test
    public void getLongUrlTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setShortUrl("GRNHv-Vd");
        when(urlShortenerService.getLongUrl(any())).thenReturn("http://www.google.com");
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        MvcResult result = mockMvc.perform(post(url).
                contentType(MediaType.APPLICATION_JSON).
                content(bodyJson)).andDo(print()).andExpect(status().isOk())
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
        mockMvc.perform(post(url).
                contentType(MediaType.APPLICATION_JSON).
                content(bodyJson)).andDo(print()).andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }

    @Test
    public void getLongUrlWithNoSuchElementExceptionTest() throws Exception {
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        when(urlShortenerService.getLongUrl(any())).thenThrow(new NoSuchElementException());
        String url = "http://localhost:8080/app/rest/long";
        String bodyJson = new ObjectMapper().writeValueAsString(shortenedUrl);
        mockMvc.perform(post(url).
                contentType(MediaType.APPLICATION_JSON).
                content(bodyJson)).andDo(print()).andExpect(status().is5xxServerError());
        verify(urlShortenerService, times(1)).getLongUrl(any());
    }
}
