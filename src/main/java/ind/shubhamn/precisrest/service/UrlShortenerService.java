package ind.shubhamn.precisrest.service;

import ind.shubhamn.precisrest.dao.UrlShortenerDAO;
import ind.shubhamn.precisrest.exception.ShortUrlAlreadyExistsException;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);

    @Autowired private UrlShortenerDAO urlShortenerDAO;

    /**
     * Shortens a URL with a custom alias or a SHA-256 hash (auto-generated alias)
     *
     * @param longUrl The URL to shorten
     * @param customAlias The custom alias to use as short URL (can be null for auto-generation)
     * @return The ShortenedUrl entity
     * @throws Exception if hashing fails
     * @throws ShortUrlAlreadyExistsException if the custom alias is already in use
     */
    public ShortenedUrl shortenUrl(String longUrl, String customAlias) throws Exception {
        logger.trace("shortenUrl called: longUrl={}, customAlias={}", longUrl, customAlias);

        logger.debug("Processing URL shortening: customAlias={}", customAlias);

        if (customAlias == null || customAlias.trim().isEmpty()) {
            logger.info("Generating auto-generated short URL using SHA-256 for: {}", longUrl);

            logger.debug("Computing SHA-256 hash for URL");

            byte[] encodedHash = getSHA256ByteArray(longUrl);
            ShortenedUrl shortenedUrl = new ShortenedUrl();
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(encodedHash);
            String shortUrl = encoded.substring(0, 8);

            logger.debug("Generated short URL: {}", shortUrl);

            shortenedUrl.setShortUrl(shortUrl);
            shortenedUrl.setLongUrl(longUrl);

            logger.info("Saving auto-generated shortened URL: {}", shortUrl);

            return saveShortenedUrl(shortenedUrl);
        }

        logger.info("Processing custom alias request: {}", customAlias);

        logger.debug("Checking if custom alias already exists: {}", customAlias);

        // Check if the custom alias already exists
        Optional<ShortenedUrl> existing = urlShortenerDAO.findByShortUrl(customAlias);
        if (existing.isPresent()) {
            logger.warn("Custom alias already exists: {}", customAlias);
            throw new ShortUrlAlreadyExistsException(customAlias);
        }

        logger.debug("Custom alias is available: {}", customAlias);

        // Create new shortened URL with custom alias
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        shortenedUrl.setShortUrl(customAlias);
        shortenedUrl.setLongUrl(longUrl);

        logger.info("Saving custom alias shortened URL: {}", customAlias);

        return saveShortenedUrl(shortenedUrl);
    }

    private byte[] getSHA256ByteArray(String longUrl) throws NoSuchAlgorithmException {
        logger.trace("Computing SHA-256 hash for: {}", longUrl);

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(longUrl.getBytes(StandardCharsets.UTF_8));

        logger.trace("SHA-256 hash computed successfully");

        return hash;
    }

    private ShortenedUrl saveShortenedUrl(ShortenedUrl shortenedUrl) {
        logger.trace("Saving shortened URL to database: {}", shortenedUrl.getShortUrl());

        logger.debug(
                "Persisting URL mapping: {} -> {}",
                shortenedUrl.getShortUrl(),
                shortenedUrl.getLongUrl());

        ShortenedUrl saved = urlShortenerDAO.save(shortenedUrl);

        logger.info("Successfully saved shortened URL: {}", saved.getShortUrl());

        return saved;
    }

    /**
     * Retrieves the original long URL from a short URL
     *
     * @param shortUrl The short URL identifier
     * @return The ShortenedUrl entity
     * @throws NoSuchElementException if the short URL is not found
     */
    public ShortenedUrl getLongUrl(String shortUrl) {
        logger.trace("getLongUrl called: shortUrl={}", shortUrl);

        logger.debug("Looking up long URL for: {}", shortUrl);

        logger.info("Retrieving long URL for short URL: {}", shortUrl);

        Optional<ShortenedUrl> shortenedUrl = urlShortenerDAO.findByShortUrl(shortUrl);

        if (shortenedUrl.isEmpty()) {
            logger.warn("Short URL not found: {}", shortUrl);
        } else {
            logger.debug("Found long URL: {}", shortenedUrl.get().getLongUrl());
            logger.info("Successfully retrieved long URL for: {}", shortUrl);
        }

        return shortenedUrl.orElseThrow();
    }
}
