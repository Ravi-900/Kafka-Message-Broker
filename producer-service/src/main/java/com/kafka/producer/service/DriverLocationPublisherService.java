package com.kafka.producer.service;

import com.kafka.producer.model.DriverLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

/**
 * Service class responsible for publishing
 * driver location updates to a Kafka topic.
 */
@Service  // Marks this class as a Spring-managed service component
public class DriverLocationPublisherService {

    /**
     * Kafka topic name injected from application.properties.
     * Example:
     * kafka.topic.driver-location=driver-location-updates
     */
    @Value("${kafka.topic.driver-location}")
    private String topic;

    /**
     * KafkaTemplate is the primary Spring abstraction
     * used to send messages to Kafka brokers.
     *
     * <String, String> means:
     *   - Key type   → String (driverId)
     *   - Value type → String (JSON payload)
     */
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * ObjectMapper is used to convert Java objects
     * into JSON strings.
     *
     * This mapper serializes the DriverLocation object
     * before sending it to Kafka.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Publishes driver location data to Kafka.
     *
     * @param location DriverLocation object received from REST API
     */
    public void publishLocation(DriverLocation location) {

        // Kafka message key
        // Using driverId ensures that all messages
        // for the same driver go to the same partition
        String key = location.getDriverId();

        // Convert DriverLocation object into JSON string
        // Kafka messages are byte-based, so objects must be serialized
        String value = mapper.writeValueAsString(location);

        // Send the message to Kafka topic
        // topic → Kafka topic name
        // key   → Used for partitioning
        // value → Actual message payload
        kafkaTemplate.send(topic, key, value);
    }
}
