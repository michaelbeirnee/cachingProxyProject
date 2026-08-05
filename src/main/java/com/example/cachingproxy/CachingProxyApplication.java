
package com.example.cachingproxy;

//import spring boot's app runner
import org.springframework.boot.SpringApplication;

//import Spring Boot app annotation
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import file tools for --clear-cache
import java.nio.file.Files;
import java.nio.file.Path;

//import list tools for translating CLI args
import java.util.ArrayList;
import java.util.List;

//class as Spring Boot starting point
@SpringBootApplication
public class CachingProxyApplication{
    //this where the app starts
    public static void main(String[] args) throws Exception{
        //translated args we will hand to Spring
        List<String> springArgs = new ArrayList<>();

        //walk through the raw CLI args
        for(int i = 0; i < args.length; i++){
            String arg = args[i];

            //--clear-cache deletes the cache file and exits without starting the server
            if(arg.equals("--clear-cache")){
                Files.deleteIfExists(Path.of(".cache"));
                System.out.println("Cache cleared.");
                return;
            }

            //--port <number> becomes Spring's --server.port=<number>
            if(arg.equals("--port") && i + 1 < args.length){
                springArgs.add("--server.port=" + args[++i]);
                continue;
            }

            //--origin <url> becomes Spring's --origin=<url>
            if(arg.equals("--origin") && i + 1 < args.length){
                springArgs.add("--origin=" + args[++i]);
                continue;
            }

            //--port=value form, still map to server.port
            if(arg.startsWith("--port=")){
                springArgs.add("--server.port=" + arg.substring("--port=".length()));
                continue;
            }

            //anything else goes straight through
            springArgs.add(arg);
        }

        SpringApplication.run(CachingProxyApplication.class, springArgs.toArray(new String[0]));
    }
}
