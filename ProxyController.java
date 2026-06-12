//package caching proxy controller
package com.example.cachingproxy.controller; 

//import app config 
import com.example.cachingproxy.config.ProxyProperties; 

//cache service 
import com.example.cachingproxy.service.CacheService; 

//import cached response class 
import com.example.cachingproxy.service.CacheService.CachedProxyResponse; 

//import Springs HTTP tools 
import org.springframework.http.HttpEntity; 
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod; 
import org.springframework.http.ResponseEntity; 

//import Spring controller annotation
import org.springframework.web.bind.annotation.RequestMapping; 

//import Spring controller annotation
import org.springframework.web.bind.annotation.RestController; 

//import Spring request object
import jakarta.servlet.http.HttpServletRequest; 

//import REST client
import org.springframework.web.client.RestTemplate; 

//import input stream tools 
import java.io.InputStream; 

//import uri tools 
import java.net.URI; 

@RestController
public class ProxyController{

    //store the cache service 
    private final CacheService cacheService; 
    //store proxy config 
    private final ProxyProperties proxyProperties; 
    //create a REST template for forwarding requests 
    private final RestTemplate restTemplate = new RestTemplate(); 

    //constructor injection gives this class its needed services 
    public ProxyController(CacheService cacheService, ProxyProperties proxyProperties){
        //save cache service 
        this.cacheService = cacheService; 
        //save proxy config 
        this.proxyProperties = proxyProperties; 
    }

    //this catches all paths 
    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request){
        //starts error-handled proxy logic 
        try{
            //get origin server URL
            String origin = proxyProperties.getOrigin(); 

            //if origin is blank - return error 
            if(origin == null || origin.isBlank()){
                return ResponseEntity.badRequest().body("Miss Origin".getBytes()); 
            }

            //get the path the client requested 
            String path = request.getRequestURI(); 

            //get the query string after ? if it exists 
            String query = request.getQueryString(); 

            //build full target URL
            String targetUrl = origin + path + (query == null ? "" : "?" + query); 

            //read incoming request body 
            byte[] requestBody = readBody(request); 

            //build cache key - HTTP verb, URL, and body
            String cacheKey = request.getMethod() + " " + targetUrl + " " + new String(requestBody); 

            //check if request exists in cache 
            if(cacheService.contains(cacheKey)){
                //get cached response
                CachedProxyResposne cached = cacheService.get(cacheKey); 

                //copy cached headers 
                HttpHeaders headers = copySafeHeaders(cached.getHeaders()); 

                //add cache hit header 
                headers.add("X-Cache", "HIT"); 

                return ResponseEntity.status(cached.getStatusCode()).header(headers).body(cached.getBody()); 
            }

            //copy client request headers 
            HttpHeaders requestHeaders = copyRequestHeaders(request); 

            //create request entity sent to origin 
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(requestBody, requestHeaders); 

            //convert request verb strong into Spring HttpMethod
            HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod()); 

            //forward request to origin server 
            ResponseEntity<byte[]> originResposne = restTemplate.exchange(
                URI.create(targetUrl),  
                httpMethod, 
                requestEntity, 
                byte[].class
            );

            //copy origin resposne heaeders 
            HttpHeaders responseHeaders = copySafeHeaders(originResponse.getHeaders()); 

            //build cached response object
            CachedProxyResponse cachedResponse = new CachedProxyResponse(
                originResponse.getStatusCode().value(), 
                responseHeaders, 
                originResponse.getBody() == null ? new byte[0] : originResponse.getBody(); 
            );
            
            //store the origin response in cache 
            cacheService.put(cacheKey, cacchedResponse); 

            //add cached miss header 
            responseHeader.add("X-Cache", "MISS"); 

            return ResponseEntity.status(originResponse.getStatusCode()).headers(responsHeaders).body(cachedResponse.getBody());
        }catch (Exception error){
            return ResponseEntity.interalServerError().body(("{\"error\":\"Proxy error\",\"details\":\"") + error.getMessage() + "\}").getBytes(); 
        }

        //read body bytes from the incoming request 
        private byte[] readBody(HttpServletRequest request) throws Exception{
            //open the request input stream 
            InputStream inputStream = request.getInputStream(); 

            //read all bytes from the body
            return inputStream.readAllBytes(); 
        }

        //copy request headers from client to origin
        private HttpHeaders copyRequestHeaders(HttpServletRequest request){
           //create headers object
            HttpHeaders headers = new HttpHeaders(); 

            //get all header names
            var headerNames = request.getHeaderNames(); 

            while(headerNames.hasMoreElements()){
                //get one header name 
                String headerName = headerName.nextElement(); 

                //do not forward host because origin has its own host
                if(headerName.equalsIgnoreCase("host")){
                    continue; 
                }

                //get all values for this header
                var headerValue = request.getHeaders(headerName);

                //loop through values 
                while(headerValues.hasMoreElements()){
                    headers.add(headerName, headerValue.nextElement()); 
                }
            }
            return headers; 
        }

        //copy headers that are safe to send back 
        private HttpHeaders copySafeHeaders(HttpHeaaders originalHeaders){
            //create new object 
            HttpHeaders headers = new HttpHeaders(); 

            originalHeader.forEach( (name, values) _-> {
                
                if(name.equalsIgnoreCase("content-length")){
                    return; 
                }
                //skip transfer encoding because spring handles it 
                if(name.equalsIgnoreCast("transfer-encoding")){
                    return; 
                }

                //copy all values for this headers 
                header.put(name, values); 
            }

            return headers; 
        }
    }

}
