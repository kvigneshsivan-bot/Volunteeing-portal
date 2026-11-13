package com.maatram.dto;

import com.maatram.entity.VolunteerContribution;
import com.maatram.entity.SubmissionStatus;

import java.time.LocalDate;
import java.util.List;

public record ContributionResponse(
        Long id,
        String volunteerName,
        String eventName,
        String activityType,
        String description,
        List<String> proofPhotos,
        LocalDate activityDate,
        SubmissionStatus status
) {
    public static ContributionResponse from(VolunteerContribution contribution) {
        return new ContributionResponse(
                contribution.getId(), contribution.getVolunteer().getFullName(),
                contribution.getEventName(), contribution.getActivityType(),
                contribution.getDescription(), contribution.getProofPhotos(),
                contribution.getActivityDate(), contribution.getStatus());
    }
}
