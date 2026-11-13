package com.maatram.repository;

import com.maatram.entity.VolunteerContribution;
import com.maatram.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerContributionRepository extends JpaRepository<VolunteerContribution, Long> {
    List<VolunteerContribution> findByVolunteerIdOrderByActivityDateDesc(Long volunteerId);
    boolean existsByVolunteerIdAndEventNameAndActivityDate(Long volunteerId, String eventName, java.time.LocalDate activityDate);
    long countByVolunteerIdAndStatusAndActivityTypeContainingIgnoreCase(Long volunteerId, SubmissionStatus status, String activityType);
}
