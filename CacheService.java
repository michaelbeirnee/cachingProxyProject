package com.example.cachingproxy.service; 

//import spring service annotation
import org.springframework.sterotype.Service; 

//import HTTP header
import org.springframework.http.HttpHeaders; 

//import java file tools                //??
import java.nio.file.Files; 

//Java path tools
import java.nio.file.Path; 

//HashMap for storing cache data
import java.util.HashMap; 

//List for storing header value 
import java.util.List; 

//Map for key-value storage
import java.util.Map; 

//Spring Service 
@Service 
public class CacheService{
    //cache file location 
    private final Path cacheFilePath = Path.of(".cache"); 

    //stores cache data while the app is running 
    private final Map<String, CachedProxyResponse> cache = new HashMap<>(); 

    //checks whether a request is already checked 
    public boolean contains(String key){
        //true if this keys exists 
        return cache.containsKey(key); 
    }

    //cached response by key
    public CachedProxyResponse get(String key){
        //return the stored response 
        return cache.get(key); 
    }

    public void put(String key, CachedProxyResponse response){
        
        //add the response to the cache 
        cache.put(key, response); 

        //to save a tiny cache status file 
        try{
            //write cache count to the file 
            Files.writeString(cacheFilePath, "Cache contains" + cache.size() + " item(s) ."); 
        }catch(Exception ignored){
            //ignore file errors because the in-app cache still works 
        }
    }

    //clears all cached data
    public void clear(){

        //clear the running app cache 
        cache.clear(); 

        //deleting the cache file
        try{
            Files.deleteIfExists(cacheFilePath); //delete if it exists
        }catch(Exeption ignored){
            //ignore file errors 
        }
    }

    //store one cached HTTP resposne
    public static class CachedProxyResponse{

        //HTTP status code
        private final int statusCode; 

        //HTTP headers
        private final HttpHeaders headers; 

        //store body bytes
        private final byte[] body;
    }
}