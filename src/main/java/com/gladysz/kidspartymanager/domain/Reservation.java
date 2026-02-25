package com.gladysz.kidspartymanager.domain;

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
    @DecimalMin("500")
    @Column(name = "price_snapshot", nullable = false)
    private BigDecimal priceSnapshot;


    @Setter
    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventAssessment eventAssessment;


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
}
