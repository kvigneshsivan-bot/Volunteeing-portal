package com.maatram.repository;

import com.maatram.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByPublishedTrueOrderByEventDateAscEventTimeAsc();
    List<Announcement> findAllByOrderByCreatedAtDesc();
}
