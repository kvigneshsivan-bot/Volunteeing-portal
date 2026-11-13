package com.maatram.repository;

import com.maatram.entity.EventRegistration;
import com.maatram.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByEventIdAndVolunteerId(Long eventId, Long volunteerId);
    long countByEventIdAndStatus(Long eventId, RegistrationStatus status);
}
