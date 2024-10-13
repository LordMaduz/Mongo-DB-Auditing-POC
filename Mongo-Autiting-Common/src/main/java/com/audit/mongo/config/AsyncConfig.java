package com.audit.mongo.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import io.cloudevents.spring.mvc.CloudEventHttpMessageConverter;
import io.micrometer.observation.ObservationRegistry;

@Configuration
public class AsyncConfig {

    @Bean
    public Executor auditListenerTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("Async-Audit-Listener-");
        executor.setKeepAliveSeconds(60);
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());
        executor.initialize();
        return executor;
    }
}
