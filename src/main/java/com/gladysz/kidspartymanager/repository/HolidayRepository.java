package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    boolean existsByDate(LocalDate date);

    boolean existsByDateBetween(LocalDate startDate, LocalDate endDate);

    void deleteByDateBefore(LocalDate date);
}

