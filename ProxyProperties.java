package com.example.cachingproxy.config; 

//import spring value annotation 
import org.springframework.beans.factory.annotation.Value; 

//import Spring component annotation
import org.springframework.stereotype.Component; 

@Component 
public class ProxyProperties{
    @Value  // this read - origin from the run command 
    private String origin; 

    //this gives other classes access to origin URL 
    public String getOrigin(){
        return origin; 
    }
}