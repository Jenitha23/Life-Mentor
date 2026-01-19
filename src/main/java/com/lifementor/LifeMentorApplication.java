// src/main/java/com/lifementor/LifeMentorApplication.java
package com.lifementor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LifeMentorApplication {
	public static void main(String[] args) {
		SpringApplication.run(LifeMentorApplication.class, args);
	}
}