package ind.shubhamn.precisrest.service;

import ind.shubhamn.precisrest.dao.UrlShortenerDAO;
import ind.shubhamn.precisrest.model.ShortenedUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlShortenerDAO urlShortenerDAO;

    public String shortenUrl(String longUrl) throws Exception {
        byte[] encodedHash = getSHA256ByteArray(longUrl);
        ShortenedUrl shortenedUrl = new ShortenedUrl();
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(encodedHash);
        shortenedUrl.setShortUrl(encoded.substring(0, 8));
        shortenedUrl.setLongUrl(longUrl);
        saveShortenedUrl(shortenedUrl);
        return shortenedUrl.getShortUrl();
    }

    private byte[] getSHA256ByteArray(String longUrl) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(
                longUrl.getBytes(StandardCharsets.UTF_8));
    }

    private void saveShortenedUrl(ShortenedUrl shortenedUrl) {
        urlShortenerDAO.save(shortenedUrl);
    }

    public String getLongUrl(String shortUrl) throws Exception {
        Optional<ShortenedUrl> shortenedUrl = urlShortenerDAO.findByShortUrl(shortUrl);
        return shortenedUrl.get().getLongUrl();
    }

}
