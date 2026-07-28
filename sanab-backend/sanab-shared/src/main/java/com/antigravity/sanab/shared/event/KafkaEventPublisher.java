package com.antigravity.sanab.shared.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Universal Distributed Event Publisher wrapping Spring KafkaTemplate.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(@Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(String topic, String key, Object payload) {
        log.info("Publishing event to Kafka topic='{}', key='{}', payload={}", topic, key, payload);
        if (kafkaTemplate != null) {
            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event to Kafka topic='{}', key='{}'", topic, key, ex);
                        } else {
                            log.info("Successfully published event to Kafka topic='{}', partition={}, offset={}",
                                    topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                        }
                    });
        } else {
            log.info("KafkaTemplate disabled/offline - processed event locally for topic='{}', key='{}'", topic, key);
        }
    }
}
