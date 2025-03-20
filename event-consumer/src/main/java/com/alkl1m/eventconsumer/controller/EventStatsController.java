package com.alkl1m.eventconsumer.controller;

import com.alkl1m.eventconsumer.model.ComplexStats;
import com.alkl1m.eventconsumer.model.EventAggregated;
import com.alkl1m.eventconsumer.model.FilterRequest;
import com.alkl1m.eventconsumer.model.GroupedStats;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class EventStatsController {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @PostMapping("/heavy")
    public List<EventAggregated> getHeavyStats(@RequestBody FilterRequest filter) {
        String sql = """
                SELECT * FROM events_aggregated
                WHERE (:eventType IS NULL OR event_type = :eventType)
                AND event_timestamp BETWEEN :start AND :end
                ORDER BY event_timestamp DESC
                LIMIT :limit
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("eventType", filter.eventType());
        params.put("start", filter.start());
        params.put("end", filter.end());
        params.put("limit", filter.limit());

        return namedParameterJdbcTemplate.query(
                sql,
                params,
                (rs, rowNum) -> new EventAggregated(
                        rs.getString("event_type"),
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getInt("count")
                )
        );
    }

    @GetMapping("/grouped-heavy")
    public List<GroupedStats> getGroupedStats() {
        String sql = """
                SELECT event_type, SUM(event_count) as total
                FROM events_aggregated
                GROUP BY event_type
                ORDER BY total DESC
                LIMIT 1000
                """;

        return namedParameterJdbcTemplate.query(
                sql,
                Collections.emptyMap(),
                (rs, rowNum) -> new GroupedStats(
                        rs.getString("event_type"),
                        rs.getInt("total")
                )
        );
    }

    @GetMapping("/complex")
    public List<ComplexStats> getComplexStats() {
        String sql = """
                WITH ranked_events AS (SELECT event_type,
                                              event_timestamp,
                                              event_count,
                                              RANK() OVER (PARTITION BY event_type ORDER BY event_count DESC) as rank
                                       FROM events_aggregated)
                SELECT event_type,
                       event_timestamp,
                       event_count
                FROM ranked_events
                WHERE rank <= 10
                """;

        return namedParameterJdbcTemplate.query(
                sql,
                Collections.emptyMap(),
                (rs, rowNum) -> new ComplexStats(
                        rs.getString("event_type"),
                        rs.getTimestamp("event_timestamp").toInstant(),
                        rs.getInt("event_count")
                )
        );
    }

}
