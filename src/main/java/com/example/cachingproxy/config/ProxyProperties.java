package com.example.cachingproxy.config;

//import spring value annotation
import org.springframework.beans.factory.annotation.Value;

//import Spring component annotation
import org.springframework.stereotype.Component;

@Component
public class ProxyProperties{
    //this reads --origin from the run command (empty string if not given)
    @Value("${origin:}")
    private String origin;

    //this gives other classes access to origin URL
    public String getOrigin(){
        return origin;
    }
}
