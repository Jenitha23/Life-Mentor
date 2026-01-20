// src/main/java/com/lifementor/config/AsyncConfig.java
package com.lifementor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // This enables async execution for email sending
}