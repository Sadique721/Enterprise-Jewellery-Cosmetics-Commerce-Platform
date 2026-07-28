package com.antigravity.sanab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * SANAB Enterprise Jewellery & Cosmetics Commerce Platform.
 *
 * <p>Modular Monolith Architecture powered by Spring Boot 4.1 & Java 25.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.antigravity.sanab")
@ConfigurationPropertiesScan(basePackages = "com.antigravity.sanab")
public class SanabApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanabApplication.class, args);
    }
}
