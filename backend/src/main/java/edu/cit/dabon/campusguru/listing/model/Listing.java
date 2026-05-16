package edu.cit.dabon.campusguru.listing.model;

import edu.cit.dabon.campusguru.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false, referencedColumnName = "user_id")
    private User tutor;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, name = "contact_info")
    private String contactInfo;

    @Column(nullable = false, name = "proof_of_competence_url")
    private String proofOfCompetenceUrl;

    @Column(name = "availability_start_date")
    private LocalDate availabilityStartDate;

    @Column(name = "availability_end_date")
    private LocalDate availabilityEndDate;

    @Column(name = "availability_daily_start_time")
    private LocalTime availabilityDailyStartTime;

    @Column(name = "availability_daily_end_time")
    private LocalTime availabilityDailyEndTime;

    @Column(name = "google_calendar_event_id")
    private String googleCalendarEventId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
