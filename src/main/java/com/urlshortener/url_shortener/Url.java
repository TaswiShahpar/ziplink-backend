package com.urlshortener.url_shortener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Url {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String longUrl;
    private String shortCode;
    @Column(columnDefinition = "datetime")
    private LocalDateTime expiryDate;
    private int clickCount=0;
    public Long getId(){return id;}
    public String getLongUrl(){return longUrl;}
    public void setLongUrl(String longUrl){this.longUrl=longUrl;}
    public String getShortCode(){return shortCode;}
    public void setShortCode(String shortCode){this.shortCode=shortCode;}
    public LocalDateTime getExpiryDate(){return expiryDate;}
    public void setExpiryDate(LocalDateTime expiryDate){this.expiryDate=expiryDate;}
    public int getClickCount(){return clickCount;}
    public void setClickCount(int clickCount){this.clickCount=clickCount;}

}
