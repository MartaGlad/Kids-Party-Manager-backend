package com.gladysz.kidspartymanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "orderers")
public class Orderer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Setter
    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;


    @Setter
    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;


    @Setter
    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;


    @Setter
    @NotBlank
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;


    @OneToMany(mappedBy = "orderer", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (! (o instanceof Orderer orderer)) return false;

        return this.id != null && this.id.equals(orderer.id);
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

        if (reservation.getOrderer() != this) {
            reservation.setOrderer(this);
        }
    }
}
