package com.example.demo.test;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;


class BnaTest {

    int count = 30;

    /**
     * https://velog.io/@nittre/%EB%B8%94%EB%A1%9C%ED%82%B9-Vs.-%EB%85%BC%EB%B8%94%EB%A1%9C%ED%82%B9-%EB%8F%99%EA%B8%B0-Vs.-%EB%B9%84%EB%8F%99%EA%B8%B0
     */
    @Test
    void blocking() {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < count; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/api/count", String.class);
            System.out.println(Thread.currentThread().getName() + " : Response : " + response.getBody());
        }
    }

    /**
     * https://velog.io/@jihoson94/EventLoopModelInSpring
     */
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
    void nioBlocking() {

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .build();

        for (int i = 0; i < count; i++) {
            String body = webClient.get()
                    .uri("/api/count")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println(Thread.currentThread().getName() + " : Response Body: " + body);
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