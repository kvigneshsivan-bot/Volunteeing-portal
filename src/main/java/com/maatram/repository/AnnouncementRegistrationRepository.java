package com.maatram.repository;

import com.maatram.entity.AnnouncementRegistration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRegistrationRepository extends JpaRepository<AnnouncementRegistration, Long> {
    Optional<AnnouncementRegistration> findByAnnouncementIdAndVolunteerId(Long announcementId, Long volunteerId);

    @EntityGraph(attributePaths = "volunteer")
    List<AnnouncementRegistration> findByAnnouncementIdOrderByCreatedAtAsc(Long announcementId);
}
