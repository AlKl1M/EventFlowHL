package com.alkl1m.eventconsumer.controller;

import com.alkl1m.eventconsumer.model.ComplexStats;
import com.alkl1m.eventconsumer.model.EventAggregated;
import com.alkl1m.eventconsumer.model.FilterRequest;
import com.alkl1m.eventconsumer.model.GroupedStats;
import com.alkl1m.eventconsumer.service.EventStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class EventStatsController {

    private final EventStatsService eventStatsService;

    @PostMapping("/heavy")
    public List<EventAggregated> getHeavyStats(@RequestBody FilterRequest filter) {
        return eventStatsService.getHeavyStats(filter);
    }

    @GetMapping("/grouped-heavy")
    public List<GroupedStats> getGroupedStats() {
        return eventStatsService.getGroupedStats();
    }

    @GetMapping("/complex")
    public List<ComplexStats> getComplexStats() {
        return eventStatsService.getComplexStats();
    }

}
