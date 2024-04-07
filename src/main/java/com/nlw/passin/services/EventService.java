package com.nlw.passin.services;

import com.nlw.passin.domain.attendee.Attendee;
import com.nlw.passin.domain.event.Event;
import com.nlw.passin.domain.event.exceptions.EventNotFoundException;
import com.nlw.passin.dto.event.EventResponseDTO;
import com.nlw.passin.repositories.EventRepository;
import com.nlw.passin.dto.event.EventIdDTO;
import com.nlw.passin.dto.event.EventRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final AttendeeService attendeeService;

    public EventResponseDTO getEventDetail(String eventId){
        Event event = this.eventRepository.findById(eventId).orElseThrow(()-> new EventNotFoundException("Event not found with ID:" + eventId));
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
}
