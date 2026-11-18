package com.maatram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "announcements", indexes = {
        @Index(name = "idx_announcement_published", columnList = "published,event_date")
})
@Getter
@Setter
@NoArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private LocalTime eventTime;

    @Column(nullable = false, length = 250)
    private String location;

    @Column(nullable = false)
    private int requiredVolunteers;

    @Column(nullable = false)
    private int registeredVolunteers;

    @Column(length = 1000)
    private String skillsOrEligibility;

    @Column(nullable = false)
    private LocalDate registrationDeadline;

    @Column(length = 16777216)
    private String imageUrl;

    @Column(length = 3000)
    private String instructions;

    @Column(nullable = false)
    private boolean published;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
