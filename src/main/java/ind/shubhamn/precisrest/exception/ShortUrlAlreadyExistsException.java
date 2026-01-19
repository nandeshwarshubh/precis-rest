package ind.shubhamn.precisrest.exception;

public class ShortUrlAlreadyExistsException extends RuntimeException {

    public ShortUrlAlreadyExistsException(String shortUrl) {
        super("Short URL '" + shortUrl + "' is already in use. Please choose a different alias.");
    }

    public ShortUrlAlreadyExistsException(String shortUrl, Throwable cause) {
        super(
                "Short URL '" + shortUrl + "' is already in use. Please choose a different alias.",
                cause);
    }
}
