package com.maatram.repository;

import com.maatram.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Long> {
    long countByZoneId(Long zoneId);
    long countByZoneIdAndActiveTrue(Long zoneId);
    long countByZoneIdAndActiveTrueAndCapacityGreaterThan(Long zoneId, int students);
}
