package com.maatram.service;

import com.maatram.dto.AnnouncementRequest;
import com.maatram.dto.AnnouncementResponse;
import com.maatram.dto.AnnouncementVolunteerResponse;
import com.maatram.entity.Announcement;
import com.maatram.entity.AnnouncementRegistration;
import com.maatram.entity.Role;
import com.maatram.entity.User;
import com.maatram.repository.AnnouncementRepository;
import com.maatram.repository.AnnouncementRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcements;
    private final AnnouncementRegistrationRepository registrations;

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> published() {
        return announcements.findByPublishedTrueOrderByEventDateAscEventTimeAsc()
                .stream().map(AnnouncementResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> all() {
        return announcements.findAllByOrderByCreatedAtDesc()
                .stream().map(AnnouncementResponse::from).toList();
    }

    @Transactional
    public AnnouncementResponse create(AnnouncementRequest request, User admin) {
        return save(new Announcement(), request, admin);
    }

    @Transactional
    public AnnouncementResponse update(Long id, AnnouncementRequest request, User admin) {
        Announcement announcement = announcements.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement was not found"));
        return save(announcement, request, admin);
    }

    @Transactional
    public AnnouncementResponse setPublished(Long id, boolean published) {
        Announcement announcement = announcements.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement was not found"));
        announcement.setPublished(published);
        return AnnouncementResponse.from(announcements.save(announcement));
    }

    @Transactional
    public void delete(Long id) {
        if (!announcements.existsById(id)) {
            throw new IllegalArgumentException("Announcement was not found");
        }
        announcements.deleteById(id);
    }

    @Transactional
    public AnnouncementResponse register(Long id, User volunteer) {
        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new IllegalStateException("Only volunteers can mark availability");
        }
        Announcement announcement = announcements.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement was not found"));
        if (!announcement.isPublished()
                || announcement.getRegistrationDeadline().isBefore(java.time.LocalDate.now())
                || announcement.getRegisteredVolunteers() >= announcement.getRequiredVolunteers()) {
            throw new IllegalStateException("This request is closed");
        }
        if (registrations.findByAnnouncementIdAndVolunteerId(id, volunteer.getId()).isPresent()) {
            throw new IllegalArgumentException("You have already marked yourself available");
        }
        AnnouncementRegistration registration = new AnnouncementRegistration();
        registration.setAnnouncement(announcement);
        registration.setVolunteer(volunteer);
        try {
            registrations.save(registration);
            announcement.setRegisteredVolunteers(announcement.getRegisteredVolunteers() + 1);
            return AnnouncementResponse.from(announcements.save(announcement));
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("You have already marked yourself available", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<AnnouncementVolunteerResponse> volunteers(Long id) {
        if (!announcements.existsById(id)) {
            throw new IllegalArgumentException("Announcement was not found");
        }
        return registrations.findByAnnouncementIdOrderByCreatedAtAsc(id).stream()
                .map(AnnouncementVolunteerResponse::from)
                .toList();
    }

    private AnnouncementResponse save(Announcement announcement, AnnouncementRequest request, User admin) {
        if (request.registrationDeadline().isAfter(request.eventDate())) {
            throw new IllegalArgumentException("Registration deadline cannot be after the event date");
        }
        announcement.setTitle(request.title().trim());
        announcement.setDescription(request.description().trim());
        announcement.setEventDate(request.eventDate());
        announcement.setEventTime(request.eventTime());
        announcement.setLocation(request.location().trim());
        announcement.setRequiredVolunteers(request.requiredVolunteers());
        announcement.setSkillsOrEligibility(request.skillsOrEligibility());
        announcement.setRegistrationDeadline(request.registrationDeadline());
        announcement.setImageUrl(request.imageUrl());
        announcement.setInstructions(request.instructions());
        announcement.setCreatedBy(admin);
        return AnnouncementResponse.from(announcements.save(announcement));
    }
}
