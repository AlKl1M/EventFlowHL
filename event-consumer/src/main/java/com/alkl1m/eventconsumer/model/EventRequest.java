package com.alkl1m.eventconsumer.model;

import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {

    private String eventType;
    private String userId;
    private Map<String, String> payload;

}
