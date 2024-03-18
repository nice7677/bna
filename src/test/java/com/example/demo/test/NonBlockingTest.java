package com.example.demo.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class NonBlockingTest {

    int count = 30;

    @Test
    void blocking() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < count; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/api/count", String.class);
            System.out.println(Thread.currentThread().getName() + " : Response : " + response.getBody());
        }
    }

    @Test
    void nonBlocking() {

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();

        for (int i = 0; i < count; i++) {
            webClient.get()
                    .uri("/api/count")
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(body -> {
                        System.out.println(Thread.currentThread().getName() + " : Response Body: " + body);
                        return Mono.just(body);
                    })
                    .subscribe();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void async() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < count; i++) {
            CompletableFuture.runAsync(() -> {
                ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/api/count", String.class);
                System.out.println(Thread.currentThread().getName() + " : Response : " + response.getBody());
            });
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}