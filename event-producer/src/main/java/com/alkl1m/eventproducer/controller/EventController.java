package com.alkl1m.eventproducer.controller;

import com.alkl1m.eventproducer.model.EventRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<String> createEvent() {
        try {
            EventRequest event = generateFakeEvent();
            String eventAsString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("raw-events", eventAsString);
            return ResponseEntity.ok("Event created with generated data");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating event: " + e.getMessage());
        }
    }

    private EventRequest generateFakeEvent() {
        EventRequest event = new EventRequest();
        event.setEventType(faker.options().option("VIEW", "CLICK", "PURCHASE", "LOGIN"));
        event.setUserId(faker.regexify("[a-zA-Z0-9]{10}"));

        Map<String, String> payload = new HashMap<>();
        payload.put("session_id", faker.regexify("[a-f0-9]{32}"));
        payload.put("ip_address", faker.internet().ipV4Address());
        payload.put("user_agent", faker.internet().userAgent());
        payload.put("item_id", faker.regexify("ITEM-[0-9]{5}"));

        event.setPayload(payload);
        return event;
    }
}