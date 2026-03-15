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


@NoArgsConstructor
@Getter
@Entity
@Table(name = "animators")
public class Animator {

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
    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;


    @Setter
    @NotBlank
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;


    @Setter
    @Column(name = "active", nullable = false)
    private boolean active = true;


    @OneToMany(mappedBy = "animator", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (! (o instanceof Animator animator)) return false;

        return this.id != null && this.id.equals(animator.id);
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

        if (reservation.getAnimator() != this) {
            reservation.setAnimator(this);
        }
    }
}


