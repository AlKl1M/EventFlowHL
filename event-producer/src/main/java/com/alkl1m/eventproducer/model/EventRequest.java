package com.alkl1m.eventproducer.model;

import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {

    private String eventType;
    private String userId;
    private Map<String, String> payload;

}
