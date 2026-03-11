package com.gladysz.kidspartymanager.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "currency_rates")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Size(min = 3, max = 3)
    @Setter
    @Column(name = "currency_code", nullable = false, length = 3, unique = true)
    private String currencyCode;


    @NotNull
    @Setter
    @Column(name = "rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal rate;


    @NotNull
    @Setter
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (! (o  instanceof CurrencyRate currencyRate)) return false;

        return this.id != null && this.id.equals(currencyRate.id);
    }


    @Override
    public int hashCode() {

        return this.id != null ? this.id.hashCode() : 0;
    }
}
