package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.EventPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventPackageRepository extends JpaRepository<EventPackage, Long> {
}
