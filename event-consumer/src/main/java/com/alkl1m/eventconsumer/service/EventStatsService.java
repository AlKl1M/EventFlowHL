package com.alkl1m.eventconsumer.service;

import com.alkl1m.eventconsumer.model.ComplexStats;
import com.alkl1m.eventconsumer.model.EventAggregated;
import com.alkl1m.eventconsumer.model.FilterRequest;
import com.alkl1m.eventconsumer.model.GroupedStats;

import java.util.List;

public interface EventStatsService {

    List<EventAggregated> getHeavyStats(FilterRequest filter);

    List<GroupedStats> getGroupedStats();

    List<ComplexStats> getComplexStats();

}
