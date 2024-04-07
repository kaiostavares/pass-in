package com.nlw.passin.services;

import com.nlw.passin.domain.attendee.Attendee;
import com.nlw.passin.domain.checkin.ChekIn;
import com.nlw.passin.dto.attendee.AttendeeDetails;
import com.nlw.passin.dto.attendee.AttendeeListResponseDTO;
import com.nlw.passin.repositories.AttendeeRepository;
import com.nlw.passin.repositories.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendeeService {
    private final AttendeeRepository attendeeRepository;
    private final CheckInRepository checkInRepository;

    public List<Attendee> getAllAttendeesFromEvent(String eventId){
        return this.attendeeRepository.findByEventId(eventId);
    }

    public AttendeeListResponseDTO getEventsAttendee(String eventId){
        List<Attendee> attendeeList = this.getAllAttendeesFromEvent(eventId);

        List<AttendeeDetails> attendeeDetailsList = attendeeList.stream().map(attendee ->{
            Optional<ChekIn> chekIn = this.checkInRepository.findByAttendeeId(attendee.getId());
            LocalDateTime checkedInAt = chekIn.<LocalDateTime>map(ChekIn::getCreatedAt).orElse(null);
            return new AttendeeDetails(attendee.getId(),attendee.getName(),attendee.getEmail(),attendee.getCreatedAt(),checkedInAt);
        }).toList();
        return new AttendeeListResponseDTO(attendeeDetailsList);
    }
}
