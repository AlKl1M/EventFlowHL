package com.alkl1m.eventconsumer.model;

import java.time.Instant;

public record AggregationKey(
        String eventType,
        Instant timestamp) {
}
