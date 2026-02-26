package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.Animator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AnimatorRepository extends JpaRepository<Animator, Long> {
}

