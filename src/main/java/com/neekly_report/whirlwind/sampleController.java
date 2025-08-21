package com.neekly_report.whirlwind;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api")
@RestController
public class sampleController {


    @GetMapping("/sample")
    ResponseEntity<?> list() {
        return ResponseEntity.ok().build();
    }

}
