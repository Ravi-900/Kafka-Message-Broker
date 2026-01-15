package com.kafka.consumer.consumer;

import com.kafka.consumer.model.DriverLocation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class NotificationConsumerService {

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.driver-location}", groupId = "${notification.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();
        DriverLocation location = objectMapper.readValue(value, DriverLocation.class);

        System.out.println("Notification Received From Producer with DriverId: "+location.getDriverId());
        System.out.println("Notification Details: { Coordinates: ["+location.getLatitude()+", "+location.getLongitude()+"], Time: "+location.getTimeStamp());
    }
}
