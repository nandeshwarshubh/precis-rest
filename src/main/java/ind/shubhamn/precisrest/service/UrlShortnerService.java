package ind.shubhamn.precisrest.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UrlShortnerService {

    public String shortenUrl(String longUrl) throws Exception {
        byte[] encodedhash = getSHA256ByteArray(longUrl);
        return Base64.encodeBase64URLSafeString(encodedhash).substring(0, 7);
    }

    private byte[] getSHA256ByteArray(String longUrl) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        return messageDigest.digest(
                longUrl.getBytes(StandardCharsets.UTF_8));
    }

}
