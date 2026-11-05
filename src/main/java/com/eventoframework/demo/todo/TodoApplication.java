package com.eventoframework.demo.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication {
    public static void main(String[] args) {
        var application = new SpringApplication(TodoApplication.class);
        application.addListeners(new StartupFailureWatchdog());
        application.run(args);
    }
}
