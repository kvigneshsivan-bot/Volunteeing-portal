package com.maatram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "volunteer_submissions")
@Getter
@Setter
@NoArgsConstructor
public class VolunteerSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private EventRegistration registration;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 500)
    private String activityProofPath;

    @Column(nullable = false, length = 500)
    private String participationProofPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(length = 1000)
    private String remarks;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    @PrePersist
    void setSubmittedAt() {
        submittedAt = Instant.now();
    }
}
