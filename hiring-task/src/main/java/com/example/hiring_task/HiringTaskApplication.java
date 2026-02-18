package com.example.hiring_task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HiringTaskApplication implements CommandLineRunner {

    private final WebhookService webhookService;

    public HiringTaskApplication(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HiringTaskApplication.class, args);
    }

    // This method runs automatically on startup
    @Override
    public void run(String... args) {
        webhookService.executeTask();
    }
}
