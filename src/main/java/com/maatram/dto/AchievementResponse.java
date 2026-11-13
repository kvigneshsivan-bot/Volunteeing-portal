package com.maatram.dto;

import com.maatram.entity.VolunteerAchievement;
import com.maatram.entity.AchievementLevel;

public record AchievementResponse(
        int schoolVisits,
        AchievementLevel schoolVisitLevel,
        int physicalVerifications,
        AchievementLevel physicalVerificationLevel
) {
    public static AchievementResponse from(VolunteerAchievement achievement) {
        return new AchievementResponse(
                achievement.getSchoolVisits(), achievement.getSchoolVisitLevel(),
                achievement.getPhysicalVerifications(), achievement.getPhysicalVerificationLevel());
    }
}
