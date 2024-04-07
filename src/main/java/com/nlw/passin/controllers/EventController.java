package com.nlw.passin.controllers;

import com.nlw.passin.dto.attendee.AttendeeListResponseDTO;
import com.nlw.passin.dto.event.EventIdDTO;
import com.nlw.passin.dto.event.EventRequestDTO;
import com.nlw.passin.dto.event.EventResponseDTO;
import com.nlw.passin.services.AttendeeService;
import com.nlw.passin.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final AttendeeService attendeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String id) {
        EventResponseDTO event = this.eventService.getEventDetail(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventIdDTO> createEvent(@RequestBody EventRequestDTO body, UriComponentsBuilder uriComponentsBuilder) {
        EventIdDTO eventIdDTO = this.eventService.createEvent(body);

        var uri = uriComponentsBuilder.path("/events/{id}").buildAndExpand(eventIdDTO.eventId()).toUri();

        return ResponseEntity.created(uri).body(eventIdDTO);
    }

    @GetMapping("/attendees/{eventId}")
    public ResponseEntity<AttendeeListResponseDTO> getEventAttendees(@PathVariable String eventId) {
        AttendeeListResponseDTO attendeeListResponseDTO = this.attendeeService.getEventsAttendee(eventId);
        return ResponseEntity.ok(attendeeListResponseDTO);
    }
}
