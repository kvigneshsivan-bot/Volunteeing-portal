package com.maatram.dto;

import com.maatram.entity.Event;
import com.maatram.entity.EventCategory;
import com.maatram.entity.EventStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        EventCategory category,
        String organisationName,
        String zoneName,
        LocalDate eventDate,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        int requiredVolunteers,
        int registeredVolunteers,
        int availableVacancies,
        LocalDate registrationDeadline,
        String eligibilityCriteria,
        String instructions,
        EventStatus status
) {
    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getOrganisation().getName(),
                event.getZone().getName(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getRequiredVolunteers(),
                event.getRegisteredVolunteers(),
                Math.max(0, event.getRequiredVolunteers() - event.getRegisteredVolunteers()),
                event.getRegistrationDeadline(),
                event.getEligibilityCriteria(),
                event.getInstructions(),
                event.getStatus());
    }
}
