package com.urlshortener.url_shortener;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url,Long>{
    Url findByShortCode(String shortCode);
    Url findByLongUrl(String LongUrl);
}