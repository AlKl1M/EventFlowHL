package com.alkl1m.eventconsumer.model;

import java.time.Instant;

public record FilterRequest(
        String eventType,
        Instant start,
        Instant end,
        int limit
) {
}
