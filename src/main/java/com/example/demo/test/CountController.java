package com.example.demo.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/count")
public class CountController {

    private static int COUNT = 0;

    @GetMapping("")
    public ResponseEntity<?> count() {
        return ResponseEntity.ok(COUNT++);
    }

}
