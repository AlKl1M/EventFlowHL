package com.alkl1m.eventconsumer.model;

import java.time.Instant;

public record ComplexStats(
        String eventType,
        Instant timestamp,
        int count
) {
}
