package ind.shubhamn.precisrest.service;

import ind.shubhamn.precisrest.dao.UrlShortenerDAO;
import ind.shubhamn.precisrest.exception.ShortUrlAlreadyExistsException;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    @Autowired private UrlShortenerDAO urlShortenerDAO;

    /**
     * Shortens a URL with a custom alias or a SHA-256 hash (auto-generated alias)
     *
     * @param longUrl The URL to shorten
     * @param customAlias The custom alias to use as short URL
     * @return The generated short URL
     * @throws Exception if hashing fails
     * @return The custom short URL
     * @throws ShortUrlAlreadyExistsException if the custom alias is already in use
     */
    public String shortenUrl(String longUrl, String customAlias) throws Exception {
        if (customAlias == null || customAlias.trim().isEmpty()) {
            byte[] encodedHash = getSHA256ByteArray(longUrl);
            ShortenedUrl shortenedUrl = new ShortenedUrl();
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(encodedHash);
            shortenedUrl.setShortUrl(encoded.substring(0, 8));
            shortenedUrl.setLongUrl(longUrl);
            saveShortenedUrl(shortenedUrl);
            return shortenedUrl.getShortUrl();
        }
        // Check if the custom alias already exists
        Optional<ShortenedUrl> existing = urlShortenerDAO.findByShortUrl(customAlias);
        if (existing.isPresent()) {
            throw new ShortUrlAlreadyExistsException(customAlias);
        }

        // Create new shortened URL with custom alias
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setShortUrl(customAlias);
        shortenedUrl.setLongUrl(longUrl);
        saveShortenedUrl(shortenedUrl);
        return shortenedUrl.getShortUrl();
    }

    private byte[] getSHA256ByteArray(String longUrl) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(longUrl.getBytes(StandardCharsets.UTF_8));
    }

    private void saveShortenedUrl(ShortenedUrl shortenedUrl) {
        urlShortenerDAO.save(shortenedUrl);
    }

    public String getLongUrl(String shortUrl) throws Exception {
        Optional<ShortenedUrl> shortenedUrl = urlShortenerDAO.findByShortUrl(shortUrl);
        return shortenedUrl.get().getLongUrl();
    }
}
