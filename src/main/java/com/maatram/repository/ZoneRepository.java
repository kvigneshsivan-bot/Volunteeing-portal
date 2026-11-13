package com.maatram.repository;

import com.maatram.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    boolean existsByNameIgnoreCase(String name);
}
