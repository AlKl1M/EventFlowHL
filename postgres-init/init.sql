DROP TABLE IF EXISTS events_aggregated;

CREATE TABLE events_aggregated
(
    event_type      VARCHAR(50) NOT NULL,
    event_timestamp TIMESTAMP   NOT NULL,
    event_count     INT         NOT NULL,
    PRIMARY KEY (event_type, event_timestamp)
);

CREATE INDEX idx_event_timestamp ON events_aggregated (event_timestamp);