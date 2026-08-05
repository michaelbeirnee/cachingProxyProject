# Caching Proxy

A CLI caching proxy server built with Spring Boot. It forwards requests to an
origin server, caches the responses, and serves repeated requests from the
cache — marking each response with an `X-Cache: HIT` or `X-Cache: MISS` header.

## Requirements

- Java 21+
- Maven

## Build

```bash
mvn package
```

This produces `target/caching-proxy-0.0.1-SNAPSHOT.jar`.

## Run

Start the proxy on a port, pointing at an origin server:

```bash
java -jar target/caching-proxy-0.0.1-SNAPSHOT.jar --port 3000 --origin http://dummyjson.com
```

Now a request to the proxy is forwarded to the origin:

```bash
curl -i http://localhost:3000/products
```

- First request → forwarded to the origin, response cached, `X-Cache: MISS`
- Same request again → served from the cache, `X-Cache: HIT`

Responses are cached per HTTP method + full URL + request body, so different
query strings or bodies get their own cache entries.

## Clear the cache

```bash
java -jar target/caching-proxy-0.0.1-SNAPSHOT.jar --clear-cache
```

(The in-memory cache also resets whenever the server restarts; this removes the
`.cache` status file the server writes.)

## Project structure

```
src/main/java/com/example/cachingproxy/
├── CachingProxyApplication.java   # entry point, CLI arg handling
├── config/ProxyProperties.java    # reads --origin
├── controller/ProxyController.java# catch-all route, forwards + caches
└── service/CacheService.java      # in-memory cache
```
