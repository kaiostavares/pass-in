package com.nlw.passin.domain.repositories;

import com.nlw.passin.domain.checkin.ChekIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<ChekIn, Integer> {
}
