package com.alkl1m.eventconsumer.repository;

import com.alkl1m.eventconsumer.model.ComplexStats;
import com.alkl1m.eventconsumer.model.EventAggregated;
import com.alkl1m.eventconsumer.model.FilterRequest;
import com.alkl1m.eventconsumer.model.GroupedStats;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EventStatsRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<EventAggregated> findFilteredEvents(FilterRequest filter) {
        String sql = """
                SELECT * FROM events_aggregated
                WHERE (CAST(:eventType AS TEXT) IS NULL OR event_type = :eventType)
                AND event_timestamp BETWEEN :start AND :end
                ORDER BY event_timestamp DESC
                LIMIT :limit
                """;

        return namedParameterJdbcTemplate.query(
                sql,
                buildParamsFromFilter(filter),
                this::mapEventAggregated
        );
    }

    public List<GroupedStats> findGroupedStats() {
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
                this::mapGroupedStats
        );
    }

    public List<ComplexStats> findRankedEvents() {
        String sql = """
                WITH ranked_events AS (
                    SELECT event_type,
                           event_timestamp,
                           event_count,
                           RANK() OVER (PARTITION BY event_type ORDER BY event_count DESC) as rank
                    FROM events_aggregated
                )
                SELECT event_type, event_timestamp, event_count
                FROM ranked_events
                WHERE rank <= 10
                """;

        return namedParameterJdbcTemplate.query(
                sql,
                Collections.emptyMap(),
                this::mapComplexStats
        );
    }

    private MapSqlParameterSource buildParamsFromFilter(FilterRequest filter) {
        return new MapSqlParameterSource()
                .addValue("eventType", filter.eventType(), Types.VARCHAR)
                .addValue("start", Timestamp.from(filter.start()), Types.TIMESTAMP)
                .addValue("end", Timestamp.from(filter.end()), Types.TIMESTAMP)
                .addValue("limit", filter.limit(), Types.INTEGER);
    }

    private EventAggregated mapEventAggregated(ResultSet rs, int rowNum) throws SQLException {
        return new EventAggregated(
                rs.getString("event_type"),
                rs.getTimestamp("event_timestamp").toInstant(),
                rs.getInt("event_count")
        );
    }

    private GroupedStats mapGroupedStats(ResultSet rs, int rowNum) throws SQLException {
        return new GroupedStats(
                rs.getString("event_type"),
                rs.getInt("total")
        );
    }

    private ComplexStats mapComplexStats(ResultSet rs, int rowNum) throws SQLException {
        return new ComplexStats(
                rs.getString("event_type"),
                rs.getTimestamp("event_timestamp").toInstant(),
                rs.getInt("event_count")
        );
    }
}