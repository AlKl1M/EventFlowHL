package com.alkl1m.eventproducer.service.impl;

import com.alkl1m.eventproducer.exception.EventProcessingException;
import com.alkl1m.eventproducer.model.EventRequest;
import com.alkl1m.eventproducer.service.EventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final List<String> EVENT_TYPES = List.of("VIEW", "CLICK", "PURCHASE", "LOGIN");
    private static final String TOPIC_NAME = "raw-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Faker faker;
    private final ObjectMapper objectMapper;

    @Override
    public void createEvent() {
        EventRequest event = generateFakeEvent();

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_NAME, eventJson);
        } catch (JsonProcessingException e) {
            throw new EventProcessingException("JSON serialization failed", e);
        }
    }

    private EventRequest generateFakeEvent() {
        EventRequest event = new EventRequest();
        event.setEventType(faker.options().option(EVENT_TYPES.toArray(new String[0])));
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