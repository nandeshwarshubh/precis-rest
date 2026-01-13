package ind.shubhamn.precisrest.dao;

import ind.shubhamn.precisrest.model.ShortenedUrl;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerDAO extends JpaRepository<ShortenedUrl, String> {
    Optional<ShortenedUrl> findByShortUrl(String shortUrl);
}
