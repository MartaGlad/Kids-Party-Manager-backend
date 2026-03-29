package com.gladysz.kidspartymanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Setter
    @Column(name = "date", nullable = false, unique = true)
    private LocalDate date;

    @NotNull
    @Setter
    @Column(name = "name", nullable = false)
    private String name;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if ( !(o instanceof Holiday holiday)) return false;

        return this.id != null && this.id.equals(holiday.id);
    }


    @Override
    public int hashCode() {

        return this.id != null ? this.id.hashCode() : 0;
    }
}





