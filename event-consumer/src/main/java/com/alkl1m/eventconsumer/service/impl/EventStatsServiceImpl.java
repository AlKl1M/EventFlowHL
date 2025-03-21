package com.alkl1m.eventconsumer.service.impl;

import com.alkl1m.eventconsumer.model.ComplexStats;
import com.alkl1m.eventconsumer.model.EventAggregated;
import com.alkl1m.eventconsumer.model.FilterRequest;
import com.alkl1m.eventconsumer.model.GroupedStats;
import com.alkl1m.eventconsumer.repository.EventStatsRepository;
import com.alkl1m.eventconsumer.service.EventStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventStatsServiceImpl implements EventStatsService {
    private final EventStatsRepository eventStatsRepository;

    @Override
    public List<EventAggregated> getHeavyStats(FilterRequest filter) {
        return eventStatsRepository.findFilteredEvents(filter);
    }

    @Override
    public List<GroupedStats> getGroupedStats() {
        return eventStatsRepository.findGroupedStats();
    }

    @Override
    public List<ComplexStats> getComplexStats() {
        return eventStatsRepository.findRankedEvents();
    }
}