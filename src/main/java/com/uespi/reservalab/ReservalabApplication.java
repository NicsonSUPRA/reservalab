package com.uespi.reservalab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReservalabApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservalabApplication.class, args);
	}

}
