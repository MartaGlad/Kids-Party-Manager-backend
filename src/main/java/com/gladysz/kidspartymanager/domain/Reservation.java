package com.gladysz.kidspartymanager.domain;

import com.gladysz.kidspartymanager.exception.ReservationChangeStatusException;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_package_id", nullable = false)
    private EventPackage eventPackage;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animator_id", nullable = false)
    private Animator animator;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderer_id", nullable = false)
    private Orderer orderer;


    @Setter
    @NotNull
    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;


    @Setter
    @Min(1)
    @Column(name = "children_count", nullable = false)
    private int childrenCount;


    @Setter
    @Min(1)
    @Column(name = "birthday_child_age", nullable = false)
    private int birthdayChildAge;


    @Setter
    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;


    @Setter
    @NotNull
    @DecimalMin("500.00")
    @Column(name = "price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSnapshot;


    @Setter
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventAssessment eventAssessment;


    public final static long CLEANUP_TIME_MINUTES = 30;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (! (o instanceof Reservation reservation)) return false;

        return this.id != null && this.id.equals(reservation.id);
    }


    @Override
    public int hashCode() {

        return this.id != null ? this.id.hashCode() : 0;
    }


    public void setEventAssessment(EventAssessment eventAssessment) {

        this.eventAssessment = eventAssessment;

        if (eventAssessment != null && eventAssessment.getReservation() != this) {
            eventAssessment.setReservation(this);
        }
    }


    private boolean isChangeStatusAllowed(Status newStatus) {

        return switch (this.status) {
            case NEW -> newStatus == Status.CONFIRMED || newStatus == Status.CANCELLED;
            case CONFIRMED -> newStatus == Status.CANCELLED || newStatus == Status.COMPLETED;
            case CANCELLED, COMPLETED -> false;
        };
    }


    public void changeStatus(Status newStatus) {

        if (newStatus == null) throw new IllegalArgumentException("New status can't be null.");

        if (newStatus == this.status) return;

        if (!isChangeStatusAllowed(newStatus))
            throw new ReservationChangeStatusException(this.status, newStatus);

        this.status = newStatus;
    }


    public boolean isActive() {

        return status == Status.NEW || status == Status.CONFIRMED;
    }


    @PrePersist
    void prePersist() {

        if (this.status == null) {
            this.status = Status.NEW;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }


    public LocalDateTime getEventEndTimeWithCleanup() {

        return eventDateTime
                .plusHours(eventPackage.getDurationHr())
                .plusMinutes(CLEANUP_TIME_MINUTES);
    }


    public boolean overlaps(LocalDateTime newReservationStart, LocalDateTime newReservationEnd) {
        return newReservationStart.isBefore(getEventEndTimeWithCleanup())
                && newReservationEnd.isAfter(eventDateTime);
    }
}
