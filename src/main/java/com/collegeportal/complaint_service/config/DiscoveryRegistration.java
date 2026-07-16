package com.collegeportal.complaint_service.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Logs Eureka registration status at startup.
 * Set eureka.client.register-with-eureka=true and configure
 * eureka.client.serviceUrl.defaultZone when a registry is available.
 */
@Configuration
public class DiscoveryRegistration {

    private static final Logger log = LoggerFactory.getLogger(DiscoveryRegistration.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${eureka.client.register-with-eureka:false}")
    private boolean registerWithEureka;

    @PostConstruct
    public void logRegistrationStatus() {
        if (registerWithEureka) {
            log.info("Service '{}' will register with Eureka.", appName);
        } else {
            log.info("Service '{}' is running in standalone mode (Eureka disabled).", appName);
        }
    }
}
