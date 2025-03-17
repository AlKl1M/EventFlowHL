package com.alkl1m.eventproducer.controller;

import com.alkl1m.eventproducer.model.EventRequest;
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

    private final KafkaTemplate<String, EventRequest> kafkaTemplate;
    private final Faker faker = new Faker();

    @PostMapping
    public ResponseEntity<String> createEvent() {
        EventRequest event = generateFakeEvent();
        kafkaTemplate.send("raw-events", event);
        return ResponseEntity.ok("Event created with generated data");
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
