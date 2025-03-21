package com.alkl1m.eventproducer.controller;

import com.alkl1m.eventproducer.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<String> createEvent() {
        try {
            eventService.createEvent();
            return ResponseEntity.ok("Event created with generated data");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error creating event: " + e.getMessage());
        }
    }
}