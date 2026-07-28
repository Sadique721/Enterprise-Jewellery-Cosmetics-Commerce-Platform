package com.antigravity.sanab.shared.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Enterprise Apache Kafka Infrastructure Configuration.
 *
 * <p>Manages distributed topics for SANAB domain events:
 * <ul>
 *   <li>{@code sanab.order-events}</li>
 *   <li>{@code sanab.payment-events}</li>
 *   <li>{@code sanab.notification-events}</li>
 *   <li>{@code sanab.inventory-events}</li>
 * </ul>
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@EnableKafka
@ConditionalOnProperty(name = "sanab.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {

    public static final String ORDER_EVENTS_TOPIC = "sanab.order-events";
    public static final String PAYMENT_EVENTS_TOPIC = "sanab.payment-events";
    public static final String NOTIFICATION_EVENTS_TOPIC = "sanab.notification-events";
    public static final String INVENTORY_EVENTS_TOPIC = "sanab.inventory-events";

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(ORDER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name(PAYMENT_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryEventsTopic() {
        return TopicBuilder.name(INVENTORY_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
