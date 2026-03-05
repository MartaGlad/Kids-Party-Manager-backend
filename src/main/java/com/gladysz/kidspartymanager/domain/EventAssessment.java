package com.gladysz.kidspartymanager.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "event_assessment")
public class EventAssessment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;


    @NotNull
    @Setter
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;


    @Setter
    @Column(name = "comment", length = 600)
    private String comment;


    @NotNull
    @Setter
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof EventAssessment eventAssessment)) return false;

        return this.id != null && this.id.equals(eventAssessment.id);
    }


    @Override
    public int hashCode() {

        return this.id != null ? this.id.hashCode() : 0;
    }


    @PrePersist
    void prePersist() {

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

}
