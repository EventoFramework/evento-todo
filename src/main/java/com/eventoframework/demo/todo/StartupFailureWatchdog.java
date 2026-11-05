package com.eventoframework.demo.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

import java.time.Duration;

/**
 * Makes sure a failed start ends the process. When the server answers too late on a cold boot,
 * Spring gives up but the JVM can stay alive with nothing on 8080: Docker sees a running container
 * and its restart policy never fires. It runs on the failure event rather than after
 * {@code run()} throws, because that throw comes after the context is closed.
 */
class StartupFailureWatchdog implements ApplicationListener<ApplicationFailedEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupFailureWatchdog.class);
    private static final Duration GRACE = Duration.ofSeconds(30);

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        log.error("Startup failed: this JVM stops within {} s so the restart policy can retry", GRACE.toSeconds());
        Thread.ofPlatform().daemon().name("startup-failure-watchdog").start(() -> {
            try {
                Thread.sleep(GRACE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Runtime.getRuntime().halt(1);
        });
    }
}
