package com.hh.oneplusplus.config;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.DeserializationException;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    @Bean
    public DefaultErrorHandler errorHandler() {
        var backoff = new ExponentialBackOffWithMaxRetries(3);
        backoff.setInitialInterval(1_000L);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(10_000L);

        ConsumerRecordRecoverer recoverer = (record, exception) -> {
            log.error("Skipping unprocessable record: topic={}, partition={}, offset={}, error={}",
                    record.topic(), record.partition(), record.offset(), exception.getMessage());
        };

        var handler = new DefaultErrorHandler(recoverer, backoff);
        handler.addNotRetryableExceptions(DeserializationException.class);

        return handler;
    }
}
