package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    Optional<CurrencyRate> findByCurrencyCode(String currencyCode);
}
