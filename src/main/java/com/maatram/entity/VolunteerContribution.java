package com.maatram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "volunteer_contributions")
@Getter
@Setter
@NoArgsConstructor
public class VolunteerContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private User volunteer;

    @Column(nullable = false, length = 180)
    private String eventName;

    @Column(nullable = false, length = 80)
    private String activityType;

    @Column(nullable = false, length = 4000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "contribution_photos", joinColumns = @JoinColumn(name = "contribution_id"))
    @Column(name = "photo_url", nullable = false, length = 1000)
    private List<String> proofPhotos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate activityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(nullable = false)
    private boolean countedForAchievement;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
