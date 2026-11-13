package com.maatram.service;

import com.maatram.dto.OrganisationRegisterRequest;
import com.maatram.entity.AccountStatus;
import com.maatram.entity.Organisation;
import com.maatram.entity.Role;
import com.maatram.entity.User;
import com.maatram.repository.OrganisationRepository;
import com.maatram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisationService {
    private final OrganisationRepository organisations;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Organisation register(OrganisationRegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String name = request.organisationName().trim();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (organisations.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Organisation name is already registered");
        }
        User owner = new User();
        owner.setFullName(request.contactName().trim());
        owner.setEmail(email);
        owner.setVolunteerId("ORG-" + Integer.toUnsignedString(email.hashCode()));
        owner.setPassword(passwordEncoder.encode(request.password()));
        owner.setRole(Role.ORGANISER);
        owner.setAccountStatus(AccountStatus.PENDING);
        owner.setActive(false);
        users.save(owner);

        Organisation organisation = new Organisation();
        organisation.setName(name);
        organisation.setDescription(request.description().trim());
        organisation.setVerificationDocumentPath(request.verificationDocumentPath().trim());
        organisation.setOwner(owner);
        return organisations.save(organisation);
    }

    public List<Organisation> pending() {
        return organisations.findByStatus(AccountStatus.PENDING);
    }

    @Transactional
    public Organisation updateStatus(Long organisationId, AccountStatus status) {
        Organisation organisation = organisations.findById(organisationId)
                .orElseThrow(() -> new IllegalArgumentException("Organisation was not found"));
        organisation.setStatus(status);
        User owner = organisation.getOwner();
        owner.setAccountStatus(status == AccountStatus.ACTIVE ? AccountStatus.ACTIVE : status);
        owner.setActive(status == AccountStatus.ACTIVE);
        users.save(owner);
        return organisations.save(organisation);
    }
}
