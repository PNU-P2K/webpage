package com.example.p2k;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class P2kApplication {

	public static void main(String[] args) {
		SpringApplication.run(P2kApplication.class, args);
	}

}
