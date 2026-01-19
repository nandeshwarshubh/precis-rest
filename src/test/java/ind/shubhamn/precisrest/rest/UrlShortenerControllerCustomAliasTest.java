package ind.shubhamn.precisrest.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ind.shubhamn.precisrest.exception.ShortUrlAlreadyExistsException;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import ind.shubhamn.precisrest.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UrlShortenerControllerCustomAliasTest {

    @Autowired private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean private UrlShortenerService urlShortenerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateShortenedUrl_WithCustomAlias_Success() throws Exception {
        // Arrange
        ShortenedUrl request = new ShortenedUrl();
        request.setLongUrl("https://www.example.com");
        request.setCustomAlias("my-link");

        when(urlShortenerService.shortenUrl(eq("https://www.example.com"), eq("my-link")))
                .thenReturn("my-link");

        // Act & Assert
        mockMvc.perform(
                        post("/app/rest/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("my-link"))
                .andExpect(jsonPath("$.longUrl").value("https://www.example.com"));

        verify(urlShortenerService, times(1))
                .shortenUrl(eq("https://www.example.com"), eq("my-link"));
    }

    @Test
    public void testCreateShortenedUrl_WithoutCustomAlias_Success() throws Exception {
        // Arrange
        ShortenedUrl request = new ShortenedUrl();
        request.setLongUrl("https://www.example.com");

        when(urlShortenerService.shortenUrl(eq("https://www.example.com"), eq(null)))
                .thenReturn("abc12345");

        // Act & Assert
        mockMvc.perform(
                        post("/app/rest/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("abc12345"))
                .andExpect(jsonPath("$.longUrl").value("https://www.example.com"));

        verify(urlShortenerService, times(1)).shortenUrl(eq("https://www.example.com"), eq(null));
    }

    @Test
    public void testCreateShortenedUrl_CustomAliasAlreadyExists_ReturnsConflict() throws Exception {
        // Arrange
        ShortenedUrl request = new ShortenedUrl();
        request.setLongUrl("https://www.example.com");
        request.setCustomAlias("existing");

        when(urlShortenerService.shortenUrl(eq("https://www.example.com"), eq("existing")))
                .thenThrow(new ShortUrlAlreadyExistsException("existing"));

        // Act & Assert
        mockMvc.perform(
                        post("/app/rest/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("ALIAS_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(409));

        verify(urlShortenerService, times(1))
                .shortenUrl(eq("https://www.example.com"), eq("existing"));
    }

    @Test
    public void testCreateShortenedUrl_InvalidCustomAlias_ReturnsBadRequest() throws Exception {
        // Arrange - custom alias with invalid characters
        ShortenedUrl request = new ShortenedUrl();
        request.setLongUrl("https://www.example.com");
        request.setCustomAlias("invalid!"); // Contains special chars

        // Act & Assert
        mockMvc.perform(
                        post("/app/rest/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        verify(urlShortenerService, never()).shortenUrl(any(), any());
    }

    @Test
    public void testCreateShortenedUrl_CustomAliasTooShort_ReturnsBadRequest() throws Exception {
        // Arrange - custom alias too short (less than 3 characters)
        ShortenedUrl request = new ShortenedUrl();
        request.setLongUrl("https://www.example.com");
        request.setCustomAlias("ab"); // Only 2 characters

        // Act & Assert
        mockMvc.perform(
                        post("/app/rest/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(urlShortenerService, never()).shortenUrl(any(), any());
    }

    @Test
    public void testCreateShortenedUrl_CustomAliasTooLong_ReturnsBadRequest() throws Exception {
        // Arrange - custom alias too long (more than 8 characters)
        ShortenedUrl request = new ShortenedUrl();
        request.setLongUrl("https://www.example.com");
        request.setCustomAlias("verylongg"); // 9 characters, exceeds 8 char limit

        // Act & Assert
        mockMvc.perform(
                        post("/app/rest/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        verify(urlShortenerService, never()).shortenUrl(any(), any());
    }
}
