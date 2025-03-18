package com.alkl1m.eventconsumer.service.impl;

import com.alkl1m.eventconsumer.model.AggregationKey;
import com.alkl1m.eventconsumer.model.EventRequest;
import com.alkl1m.eventconsumer.service.EventAggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAggregatorImpl implements EventAggregator {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @KafkaListener(
            topics = "raw-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            List<ConsumerRecord<String, String>> records,
            Acknowledgment acknowledgment
    ) {
        for (ConsumerRecord<String, String> record : records) {
            try {
                EventRequest event = objectMapper.readValue(record.value(), EventRequest.class);
                AggregationKey key = new AggregationKey(
                        event.getEventType(),
                        Instant.now().truncatedTo(ChronoUnit.MINUTES)
                );

                MapSqlParameterSource parameters = new MapSqlParameterSource()
                        .addValue("eventType", key.eventType())
                        .addValue("eventTimestamp", Timestamp.from(key.timestamp()))  // Explicit conversion
                        .addValue("eventCount", 1);

                namedParameterJdbcTemplate.update(
                        "INSERT INTO events_aggregated (event_type, event_timestamp, event_count) " +
                                "VALUES (:eventType, :eventTimestamp, :eventCount) " +
                                "ON CONFLICT (event_type, event_timestamp) " +
                                "DO UPDATE SET event_count = events_aggregated.event_count + EXCLUDED.event_count",
                        parameters
                );
            } catch (Exception e) {
                System.err.println("Error processing record at offset " + record.offset() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        acknowledgment.acknowledge();
    }
}