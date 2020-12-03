package org.openmbee.syncservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
@ComponentScan({"org.openmbee.syncservice"})
public class SyncServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(SyncServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SyncServiceApplication.class, args);
    }

    @Value("${spring.webclient.bufferbytes:16777216}")
    private Integer maxBufferSize;


    @Bean
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(maxBufferSize))
                .build());
    }





}
