package com.maatram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "volunteer_achievements")
@Getter
@Setter
@NoArgsConstructor
public class VolunteerAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "volunteer_id", nullable = false, unique = true)
    private User volunteer;

    @Column(nullable = false)
    private int schoolVisits;

    @Column(nullable = false)
    private int physicalVerifications;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AchievementLevel schoolVisitLevel = AchievementLevel.STARTER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AchievementLevel physicalVerificationLevel = AchievementLevel.STARTER;
}
