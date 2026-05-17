package com.urlshortener.url_shortener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


import com.urlshortener.url_shortener.UrlRepository;

@CrossOrigin(origins="https://ziplink-frontend-qovk.vercel.app")
@RestController
public class UrlController {

    @Autowired
    private UrlRepository urlRepository;
    
    private java.util.concurrent.ConcurrentHashMap<String,java.util.concurrent.atomic.AtomicInteger> requestCount=new java.util.concurrent.ConcurrentHashMap<>();
    private java.util.concurrent.ConcurrentHashMap<String,Long> requestTime=new java.util.concurrent.ConcurrentHashMap<>();


    @GetMapping("/")
    public String home() {
        return "URL Shortener is running!";
    }@GetMapping("/stats/{code}")
    public String getStats(@PathVariable String code){
        Url url=urlRepository.findByShortCode(code);
        if(url==null){
            return "URL not found!";
        }
        return "Short Code: "+url.getShortCode()+
               "\nOriginal URL: "+url.getLongUrl()+
               "\nClick Count: "+url.getClickCount()+
               "\nExpiry Date: "+url.getExpiryDate();
    }
    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody java.util.Map<String,String> body,
                             jakarta.servlet.http.HttpServletRequest request){
       String ip=request.getRemoteAddr();
       if(isRateLimited(ip)){
        return "Error: Rate limite exceeded! Max 5 requests per minute.";
       }
       String longUrl=body.get("longUrl");
       String customAlias=body.get("customAlias");
       Url existing =urlRepository.findByLongUrl(longUrl);
       if(existing!=null){
          if(existing.getExpiryDate()!=null &&
             existing.getExpiryDate().isAfter(LocalDateTime.now())){
             return "Already exists: myapp.com/"+existing.getShortCode();

          }else{
            urlRepository.delete(existing);
          }
        
       } 
       String shortCode;
       if(customAlias!=null && !customAlias.isEmpty()){
        Url aliasCheck=urlRepository.findByShortCode(customAlias);
        if(aliasCheck!=null){
            return "Error: Custom alias already taken!";
        }
        shortCode=customAlias;

       }
       else{
        shortCode=generateCode(6);
       }
       Url url=new Url();
       url.setLongUrl(longUrl);
       url.setShortCode(shortCode);
       url.setExpiryDate(LocalDateTime.now().plusDays(7));
       urlRepository.save(url);
       return "Short URL: myapp.com/"+shortCode+" (expires in 7 days)";

    }
    @GetMapping("/{code}")
    public RedirectView redirect(@PathVariable String code){
        Url url=urlRepository.findByShortCode(code);
        if(url==null){
            return new RedirectView("/");
        }
        if(url.getExpiryDate().isBefore(LocalDateTime.now())){
            urlRepository.delete(url);
            return new RedirectView("/");
        }
        url.setClickCount(url.getClickCount()+1);
        urlRepository.save(url);
        return new RedirectView(url.getLongUrl());
    }
    private boolean isRateLimited(String ip){
        long currentTime=System.currentTimeMillis();
        requestTime.putIfAbsent(ip,currentTime);
        requestCount.putIfAbsent(ip,new java.util.concurrent.atomic.AtomicInteger(0));
        if( currentTime-requestTime.get(ip)>60000){
            requestTime.put(ip,currentTime);
            requestCount.get(ip).set(0);
        }
        return requestCount.get(ip).incrementAndGet()>5;
    }
    private String generateCode(int length){
        String characters="abcdefghijklmnopqrstuvwxyz0123456789";
        String result="";
        for(int i=0;i<length;i++){
            int randomIndex=(int)(Math.random()*characters.length());
            result=result+characters.charAt(randomIndex);
        }
        return result;
    }
}