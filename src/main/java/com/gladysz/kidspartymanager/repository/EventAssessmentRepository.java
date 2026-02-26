package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.EventAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAssessmentRepository extends JpaRepository<EventAssessment, Long> {
}
