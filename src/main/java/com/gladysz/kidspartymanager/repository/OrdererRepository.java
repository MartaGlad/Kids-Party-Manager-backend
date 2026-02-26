package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.Orderer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdererRepository extends JpaRepository<Orderer, Long> {
}
