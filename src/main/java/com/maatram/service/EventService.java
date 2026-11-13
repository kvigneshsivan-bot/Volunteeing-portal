package com.maatram.service;

import com.maatram.dto.EventRequest;
import com.maatram.dto.EventResponse;
import com.maatram.entity.*;
import com.maatram.repository.EventRegistrationRepository;
import com.maatram.repository.EventRepository;
import com.maatram.repository.OrganisationRepository;
import com.maatram.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository events;
    private final EventRegistrationRepository registrations;
    private final OrganisationRepository organisations;
    private final ZoneRepository zones;

    @Transactional
    public EventResponse create(EventRequest request, User organiser) {
        Organisation organisation = organisations.findByOwnerId(organiser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Organisation profile was not found"));
        if (organiser.getRole() != Role.ORGANISER || organisation.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Only an approved organiser can create events");
        }
        if (request.endTime().isBefore(request.startTime())) {
            throw new IllegalArgumentException("Event end time must be after its start time");
        }
        if (request.registrationDeadline().isAfter(request.eventDate())) {
            throw new IllegalArgumentException("Registration deadline cannot be after the event date");
        }
        Event event = new Event();
        event.setTitle(request.title().trim());
        event.setDescription(request.description().trim());
        event.setCategory(request.category());
        event.setZone(zones.findById(request.zoneId())
                .orElseThrow(() -> new IllegalArgumentException("Zone was not found")));
        event.setOrganisation(organisation);
        event.setEventDate(request.eventDate());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setLocation(request.location().trim());
        event.setRequiredVolunteers(request.requiredVolunteers());
        event.setRegistrationDeadline(request.registrationDeadline());
        event.setEligibilityCriteria(request.eligibilityCriteria());
        event.setInstructions(request.instructions());
        return EventResponse.from(events.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> approvedEvents() {
        return events.findByStatusOrderByEventDateAsc(EventStatus.APPROVED).stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> organiserEvents(User organiser) {
        return events.findByOrganisationOwnerIdOrderByEventDateAsc(organiser.getId()).stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional
    public EventResponse register(Long eventId, User volunteer) {
        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new IllegalStateException("Only volunteers can register for events");
        }
        Event event = events.findByIdForUpdate(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event was not found"));
        if (event.getStatus() != EventStatus.APPROVED || event.getRegistrationDeadline().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Registration is closed for this event");
        }
        if (registrations.findByEventIdAndVolunteerId(eventId, volunteer.getId()).isPresent()) {
            throw new IllegalArgumentException("You are already registered for this event");
        }
        if (event.getRegisteredVolunteers() >= event.getRequiredVolunteers()) {
            throw new IllegalStateException("Event registration is full");
        }
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setVolunteer(volunteer);
        try {
            registrations.save(registration);
            event.setRegisteredVolunteers(event.getRegisteredVolunteers() + 1);
            events.save(event);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("You are already registered for this event", exception);
        }
        return EventResponse.from(event);
    }

    @Transactional
    public EventResponse updateStatus(Long eventId, EventStatus status) {
        Event event = events.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event was not found"));
        event.setStatus(status);
        return EventResponse.from(events.save(event));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> pendingEvents() {
        return events.findByStatusOrderByEventDateAsc(EventStatus.PENDING_APPROVAL).stream()
                .map(EventResponse::from)
                .toList();
    }
}
