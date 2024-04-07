package com.nlw.passin.repositories;

import com.nlw.passin.domain.checkin.ChekIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckInRepository extends JpaRepository<ChekIn, Integer> {
    Optional<ChekIn> findByAttendeeId(String attendeeID);
}
