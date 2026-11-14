package com.maatram.dto;

import com.maatram.entity.AnnouncementRegistration;

public record AnnouncementVolunteerResponse(
        Long id,
        String name,
        String mobileNumber,
        String academicYear,
        String email
) {
    public static AnnouncementVolunteerResponse from(AnnouncementRegistration registration) {
        return new AnnouncementVolunteerResponse(
                registration.getVolunteer().getId(),
                registration.getVolunteer().getFullName(),
                registration.getVolunteer().getMobileNumber(),
                registration.getVolunteer().getAcademicYear(),
                registration.getVolunteer().getEmail());
    }
}
