package com.nlw.passin.dto.attendee;

import java.util.List;

public record AttendeeListResponseDTO(
        List<AttendeeDetails> attendees, int total
) {
}
