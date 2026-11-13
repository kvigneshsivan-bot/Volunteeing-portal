package com.maatram.service;

import com.maatram.dto.AnnouncementRequest;
import com.maatram.dto.AnnouncementResponse;
import com.maatram.entity.Announcement;
import com.maatram.entity.User;
import com.maatram.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcements;

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
