package com.example.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application class for the Agent Demo Spring Boot application.
 * 
 * This class serves as the entry point for the Spring Boot application.
 * The @SpringBootApplication annotation enables auto-configuration, component scanning,
 * and configuration properties.
 * 
 * @author Portal Team
 * @version 1.0
 * @since 2025-07-24
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example.portal")
public class PortalApplication {

    /**
     * Main method that serves as the entry point for the Spring Boot application.
     * 
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }
}
