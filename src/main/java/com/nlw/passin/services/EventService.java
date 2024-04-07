package com.nlw.passin.services;

import com.nlw.passin.domain.attendee.Attendee;
import com.nlw.passin.domain.event.Event;
import com.nlw.passin.domain.event.exceptions.EventFullException;
import com.nlw.passin.domain.event.exceptions.EventNotFoundException;
import com.nlw.passin.dto.attendee.AttendeeIdDTO;
import com.nlw.passin.dto.attendee.AttendeeRequestDTO;
import com.nlw.passin.dto.event.EventResponseDTO;
import com.nlw.passin.repositories.EventRepository;
import com.nlw.passin.dto.event.EventIdDTO;
import com.nlw.passin.dto.event.EventRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId){
        Event event = getEventById(eventId);
        List<Attendee> attendeeList = attendeeService.getAllAttendeesFromEvent(eventId);
        return new EventResponseDTO(event, attendeeList.size());
    }
    public EventIdDTO createEvent(EventRequestDTO eventDTO){
        Event newEvent = Event.builder()
            .title(eventDTO.title())
            .details(eventDTO.details())
            .maximumAttendees(eventDTO.maximumAttendees())
            .slug(this.createSlug(eventDTO.title()))
        .build();

        this.eventRepository.save(newEvent);
        return new EventIdDTO(newEvent.getId());
    }

    private String createSlug(String text){
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCOMBINING_DIACRITICAL_MARKS}]","")
                         .replaceAll("[^\\w\\s]", "")
                         .replaceAll("\\s+","-")
                         .toLowerCase();
    }

    public AttendeeIdDTO registerAttendeeOnEvent(String eventId, AttendeeRequestDTO attendeeRequestDTO){
        this.attendeeService.verifyAttendeeSubscription(attendeeRequestDTO.email(), eventId);

        Event event = this.getEventById(eventId);
        List<Attendee> attendeeList = this.attendeeService.getAllAttendeesFromEvent(eventId);
        if(event.getMaximumAttendees() <= attendeeList.size()) throw new EventFullException("Event is full");

        Attendee newAttendee = Attendee.builder()
        .name(attendeeRequestDTO.name())
        .email(attendeeRequestDTO.email())
        .event(event)
        .createdAt(LocalDateTime.now())
        .build();
        this.attendeeService.registerAttendee(newAttendee);

        return new AttendeeIdDTO(newAttendee.getId());
    }

    private Event getEventById(String eventId){
        return this.eventRepository.findById(eventId).orElseThrow(()-> new EventNotFoundException("Event not found with ID:" + eventId));
    }

}
