package com.maatram.repository;

import com.maatram.entity.VolunteerAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerAchievementRepository extends JpaRepository<VolunteerAchievement, Long> {
    Optional<VolunteerAchievement> findByVolunteerId(Long volunteerId);
}
