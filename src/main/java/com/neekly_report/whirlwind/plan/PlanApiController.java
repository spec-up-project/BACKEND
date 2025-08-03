package com.neekly_report.whirlwind.plan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanApiController {
    private final PlanService planService;

    @GetMapping("list")
    ResponseEntity<?> list() {
        return ResponseEntity.ok(
                planService.getPlanList("jihee")
        );
    }

}
