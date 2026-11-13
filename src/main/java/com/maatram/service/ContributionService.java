package com.maatram.service;

import com.maatram.dto.AchievementResponse;
import com.maatram.dto.ContributionRequest;
import com.maatram.dto.ContributionResponse;
import com.maatram.entity.*;
import com.maatram.repository.VolunteerAchievementRepository;
import com.maatram.repository.VolunteerContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {
    private final VolunteerContributionRepository contributions;
    private final VolunteerAchievementRepository achievements;

    @Transactional
    public ContributionResponse create(ContributionRequest request, User volunteer) {
        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new IllegalStateException("Only volunteers can post contributions");
        }
        if (contributions.existsByVolunteerIdAndEventNameAndActivityDate(
                volunteer.getId(), request.eventName().trim(), request.activityDate())) {
            throw new IllegalArgumentException("This activity has already been posted");
        }
        VolunteerContribution contribution = new VolunteerContribution();
        contribution.setVolunteer(volunteer);
        contribution.setEventName(request.eventName().trim());
        contribution.setActivityType(request.activityType().trim());
        contribution.setDescription(request.description().trim());
        contribution.setProofPhotos(List.copyOf(request.proofPhotos()));
        contribution.setActivityDate(request.activityDate());
        return ContributionResponse.from(contributions.save(contribution));
    }

    @Transactional(readOnly = true)
    public List<ContributionResponse> mine(User volunteer) {
        return contributions.findByVolunteerIdOrderByActivityDateDesc(volunteer.getId())
                .stream().map(ContributionResponse::from).toList();
    }

    @Transactional
    public ContributionResponse updateStatus(Long id, SubmissionStatus status) {
        VolunteerContribution contribution = contributions.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contribution was not found"));
        if (contribution.getStatus() != SubmissionStatus.APPROVED && status == SubmissionStatus.APPROVED) {
            contribution.setCountedForAchievement(false);
        }
        contribution.setStatus(status);
        if (status == SubmissionStatus.APPROVED && !contribution.isCountedForAchievement()) {
            VolunteerAchievement achievement = achievements.findByVolunteerId(contribution.getVolunteer().getId())
                    .orElseGet(() -> newAchievement(contribution.getVolunteer()));
            if (contribution.getActivityType().toLowerCase().contains("school visit")) {
                achievement.setSchoolVisits(achievement.getSchoolVisits() + 1);
                achievement.setSchoolVisitLevel(levelFor(achievement.getSchoolVisits(), 10, 25, 45));
            }
            if (contribution.getActivityType().toLowerCase().contains("physical verification")) {
                achievement.setPhysicalVerifications(achievement.getPhysicalVerifications() + 1);
                achievement.setPhysicalVerificationLevel(levelFor(achievement.getPhysicalVerifications(), 25, 50, 75));
            }
            achievement.setVolunteer(contribution.getVolunteer());
            achievements.save(achievement);
            contribution.setCountedForAchievement(true);
        }
        return ContributionResponse.from(contributions.save(contribution));
    }

    @Transactional(readOnly = true)
    public AchievementResponse achievement(User volunteer) {
        return achievements.findByVolunteerId(volunteer.getId())
                .map(AchievementResponse::from)
                .orElseGet(() -> new AchievementResponse(0, AchievementLevel.STARTER, 0, AchievementLevel.STARTER));
    }

    private VolunteerAchievement newAchievement(User volunteer) {
        VolunteerAchievement achievement = new VolunteerAchievement();
        achievement.setVolunteer(volunteer);
        return achievement;
    }

    private AchievementLevel levelFor(int count, int bronze, int silver, int gold) {
        if (count >= gold) return AchievementLevel.GOLD;
        if (count >= silver) return AchievementLevel.SILVER;
        if (count >= bronze) return AchievementLevel.BRONZE;
        return AchievementLevel.STARTER;
    }
}
