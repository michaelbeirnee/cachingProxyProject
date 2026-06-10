
package com.example.cachingproxy; 

//import spring boot's app runner 
import org.springframework.boot.SpringApplication; 

//import Spring Boot app annoatation
import org.springframework.boot.autoconfigure.SpringBootApplication; 

//class as Spring Boot starting point 
@SpringBootApplication
public class CachingProxyApplication{  
    //this where the app starts 
    public static void main(String[] args){
        SpringApplication.run(CachingProxyApplication.class, args); 
    }
}