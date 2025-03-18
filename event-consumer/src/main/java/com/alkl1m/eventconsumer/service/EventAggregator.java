package com.alkl1m.eventconsumer.service;

import com.alkl1m.eventconsumer.model.EventRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface EventAggregator {

    void consume(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment);

}
