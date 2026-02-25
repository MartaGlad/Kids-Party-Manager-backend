package com.gladysz.kidspartymanager.domain;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "event_packages")
public class EventPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @Setter
    @NotBlank
    @Size(max = 500)
    @Column(name = "description", nullable = false, length = 500)
    private String description;


    @Setter
    @NotNull
    @DecimalMin("500")
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;


    @Setter
    @Min(1)
    @Column(name = "max_children_count", nullable = false)
    private int maxChildrenCount;


    @Setter
    @Min(1)
    @Column(name = "duration_in_hours", nullable = false)
    private int durationHr;


    @OneToMany(mappedBy = "eventPackage", fetch = FetchType.LAZY)
    private final List<Reservation> reservations = new ArrayList<>();


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof EventPackage eventPackage)) return false;

        return this.id != null && this.id.equals(eventPackage.id);
    }


    @Override
    public int hashCode() {

        return this.id != null ? this.id.hashCode() : 0;
    }


    public void addReservation(Reservation reservation) {
        if (reservation == null) return;

        if (!this.reservations.contains(reservation)) {
            this.reservations.add(reservation);
        }
        if (reservation.getEventPackage() != this) {
            reservation.setEventPackage(this);
        }
    }
}
