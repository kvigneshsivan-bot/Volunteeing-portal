package com.maatram.service;

import com.maatram.dto.CollegeRequest;
import com.maatram.dto.ZoneRequest;
import com.maatram.entity.College;
import com.maatram.entity.Zone;
import com.maatram.repository.CollegeRepository;
import com.maatram.repository.OrganisationRepository;
import com.maatram.repository.UserRepository;
import com.maatram.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final ZoneRepository zones;
    private final CollegeRepository colleges;
    private final OrganisationRepository organisations;
    private final UserRepository users;

    public List<Zone> listZones() {
        return zones.findAll();
    }

    @Transactional
    public Zone createZone(ZoneRequest request) {
        if (zones.existsByNameIgnoreCase(request.name().trim())) {
            throw new IllegalArgumentException("Zone name is already in use");
        }
        Zone zone = new Zone();
        zone.setName(request.name().trim());
        zone.setMaxColleges(request.maxColleges());
        zone.setMaxStudents(request.maxStudents());
        return zones.save(zone);
    }

    @Transactional
    public College createCollege(CollegeRequest request) {
        Zone zone = zones.findById(request.zoneId())
                .orElseThrow(() -> new IllegalArgumentException("Zone was not found"));
        if (!zone.isActive()) {
            throw new IllegalStateException("Cannot add a college to an inactive zone");
        }
        if (colleges.countByZoneIdAndActiveTrue(zone.getId()) >= zone.getMaxColleges()) {
            throw new IllegalStateException("Zone college capacity has been reached");
        }
        if (request.capacity() > zone.getMaxStudents()) {
            throw new IllegalArgumentException("College capacity cannot exceed zone capacity");
        }
        College college = new College();
        college.setName(request.name().trim());
        college.setCapacity(request.capacity());
        college.setZone(zone);
        return colleges.save(college);
    }

    public List<College> listColleges() {
        return colleges.findAll();
    }

    public List<com.maatram.entity.Organisation> pending() {
        return organisations.findByStatus(com.maatram.entity.AccountStatus.PENDING);
    }

    @Transactional
    public com.maatram.entity.Organisation updateStatus(Long organisationId, com.maatram.entity.AccountStatus status) {
        com.maatram.entity.Organisation organisation = organisations.findById(organisationId)
                .orElseThrow(() -> new IllegalArgumentException("Organisation was not found"));
        organisation.setStatus(status);
        com.maatram.entity.User owner = organisation.getOwner();
        owner.setAccountStatus(status == com.maatram.entity.AccountStatus.ACTIVE
                ? com.maatram.entity.AccountStatus.ACTIVE : status);
        owner.setActive(status == com.maatram.entity.AccountStatus.ACTIVE);
        users.save(owner);
        return organisations.save(organisation);
    }
}
