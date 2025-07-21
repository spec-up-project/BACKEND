package com.neekly_report.whirlwind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WhirlwindApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhirlwindApplication.class, args);
	}

}
