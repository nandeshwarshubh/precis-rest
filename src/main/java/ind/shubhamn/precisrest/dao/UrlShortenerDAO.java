package ind.shubhamn.precisrest.dao;

import ind.shubhamn.precisrest.model.ShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenerDAO extends JpaRepository<ShortenedUrl, String> {
    Optional<ShortenedUrl> findByShortUrl(String shortUrl);
}
