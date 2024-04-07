package com.nlw.passin.dto.event;

import com.nlw.passin.domain.event.Event;
import lombok.Getter;

@Getter
public class EventResponseDTO {

    EventDetailDTO event;

    public  EventResponseDTO(Event event, Integer attendeesAmount ){
        this.event = new EventDetailDTO(
            event.getId(),
            event.getTitle(),
            event.getDetails(),
            event.getSlug(),
            event.getMaximumAttendees(),
            attendeesAmount
        );
    }
}
