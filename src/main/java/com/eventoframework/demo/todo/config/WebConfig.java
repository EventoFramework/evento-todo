package com.eventoframework.demo.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // frontend server address
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // allowed request methods
                .allowedHeaders("*") // allowed headers
        //.allowCredentials(true)
        ;
    }
}