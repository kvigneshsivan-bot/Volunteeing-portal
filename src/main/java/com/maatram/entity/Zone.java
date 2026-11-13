package com.maatram.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "zones", uniqueConstraints = @UniqueConstraint(name = "uk_zone_name", columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private int maxColleges = 4;

    @Column(nullable = false)
    private int maxStudents = 1200;

    @Column(nullable = false)
    private boolean active = true;
}
