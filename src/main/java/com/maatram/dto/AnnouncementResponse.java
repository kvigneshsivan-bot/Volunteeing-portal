package com.maatram.dto;

import com.maatram.entity.Announcement;

import java.time.LocalDate;
import java.time.LocalTime;

public record AnnouncementResponse(
        Long id,
        String title,
        String description,
        LocalDate eventDate,
        LocalTime eventTime,
        String location,
        int requiredVolunteers,
        int registeredVolunteers,
        String skillsOrEligibility,
        LocalDate registrationDeadline,
        String imageUrl,
        String instructions,
        boolean published
) {
    public static AnnouncementResponse from(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(), announcement.getTitle(), announcement.getDescription(),
                announcement.getEventDate(), announcement.getEventTime(), announcement.getLocation(),
                announcement.getRequiredVolunteers(), announcement.getRegisteredVolunteers(),
                announcement.getSkillsOrEligibility(), announcement.getRegistrationDeadline(),
                announcement.getImageUrl(), announcement.getInstructions(), announcement.isPublished());
    }
}
