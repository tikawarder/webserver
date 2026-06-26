package reactiveservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class HelloController {

    // Mono: 0 or 1 element — like Optional, but async
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello from ReactiveService!");
    }

    // Flux: 0..N elements — like a Stream, but async and lazy
    // TEXT_EVENT_STREAM_VALUE tells the browser: keep the connection open, elements arrive over time
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return Flux.just("one", "two", "three")
                .delayElements(Duration.ofMillis(500));
    }

    // Mono with transformation — shows the pipeline style
    @GetMapping("/hello/upper")
    public Mono<String> helloUpper() {
        return Mono.just("hello from reactive")
                .map(String::toUpperCase)
                .map(s -> s + "!");
    }

    // Flux with filter and map — same operators as Java Stream, but lazy until subscribed
    @GetMapping("/stream/filtered")
    public Flux<String> streamFiltered() {
        return Flux.just("apple", "banana", "avocado", "cherry", "apricot")
                .filter(s -> s.startsWith("a"))
                .map(String::toUpperCase);
    }
}
