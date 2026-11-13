package com.maatram.repository;

import com.maatram.entity.AccountStatus;
import com.maatram.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Organisation> findByOwnerId(Long ownerId);
    List<Organisation> findByStatus(AccountStatus status);
}
