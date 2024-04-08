package com.nlw.passin.services;

import com.nlw.passin.domain.attendee.Attendee;
import com.nlw.passin.domain.attendee.exceptions.AttendeeNotFoundException;
import com.nlw.passin.domain.checkin.CheckIn;
import com.nlw.passin.dto.attendee.AttendeeBadgeDTO;
import com.nlw.passin.dto.attendee.AttendeeBadgeResponseDTO;
import com.nlw.passin.dto.attendee.AttendeeDetails;
import com.nlw.passin.dto.attendee.AttendeeListResponseDTO;
import com.nlw.passin.domain.attendee.exceptions.AttendeeAlreadyExistException;
import com.nlw.passin.repositories.AttendeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final CheckInService checkInService;

    public List<Attendee> getAllAttendeesFromEvent(String eventId){
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeeListResponseDTO getEventsAttendee(String eventId, int page, String query){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);
        if (query != null && !query.isEmpty()) {
            attendeeList = attendeeList.stream()
                    .filter(attendee -> attendee.getName().toLowerCase().contains(query.toLowerCase()))
                    .toList();
        }
        List<Attendee> paginatedAttendeeList = attendeeList.stream()
                .skip(page * 10)
                .limit(10)
                .toList();
        List<AttendeeDetails> attendeeDetailsList = paginatedAttendeeList.stream().map(attendee ->{
            Optional<CheckIn> chekIn = this.checkInService.getCheckIn(attendee.getId());
            LocalDateTime checkedInAt = chekIn.<LocalDateTime>map(CheckIn::getCreatedAt).orElse(null);
            return new AttendeeDetails(attendee.getId(),attendee.getName(),attendee.getEmail(),attendee.getCreatedAt(),checkedInAt);
        }).toList();
        return new AttendeeListResponseDTO(attendeeDetailsList, attendeeList.size());
    }

    public void verifyAttendeeSubscription(String email, String eventId){
        Optional<Attendee> isAttendeeRegistered = this.attendeeRepository.findByEventIdAndEmail(eventId, email);
        if(isAttendeeRegistered.isPresent()) throw new AttendeeAlreadyExistException("Attendee already registered");
    }

    public Attendee registerAttendee(Attendee newAttendee){
        this.attendeeRepository.save(newAttendee);
        return newAttendee;
    }

    public void checkInAttendee(String attendeeId){
        Attendee attendee = this.getAttendee(attendeeId);
        this.checkInService.registerCheckIn(attendee);
    }

    private Attendee getAttendee(String attendeeId){
        return this.attendeeRepository.findById(attendeeId).orElseThrow(()->new AttendeeNotFoundException("Attendee not found with ID:" + attendeeId));
    }

    public AttendeeBadgeResponseDTO getAttendeeBadge(String attendeeId, UriComponentsBuilder uriComponentsBuilder){
        Attendee attendee = this.getAttendee(attendeeId);
        var uri = uriComponentsBuilder.path("attendees/{attendeeId}/check-in").buildAndExpand(attendeeId).toUri().toString();

        AttendeeBadgeDTO attendeeBadgeDTO = new AttendeeBadgeDTO(attendee.getName(),attendee.getEmail(),uri,attendee.getEvent().getId());
        return new AttendeeBadgeResponseDTO(attendeeBadgeDTO);
    }
}
