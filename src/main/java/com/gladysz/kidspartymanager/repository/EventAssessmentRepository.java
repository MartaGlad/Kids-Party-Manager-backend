package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.EventAssessment;
import com.gladysz.kidspartymanager.dto.animator.AnimatorRatingResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventAssessmentRepository extends JpaRepository<EventAssessment, Long> {

    @Query("""
           select count(e)
           from EventAssessment e
           where e.reservation.animator.id = :animatorId
          """)
    long countReservationRatingsByAnimatorId(@Param("animatorId") Long animatorId);


    @Query("""
           select avg(e.rating)
           from EventAssessment e
           where e.reservation.animator.id = :animatorId
           """)
    Double findAverageReservationRatingByAnimatorId(@Param("animatorId") Long animatorId);


    @Query("""
            select new com.gladysz.kidspartymanager.dto.animator.AnimatorRatingResponseDto(
                       e.reservation.animator.id,
                       avg(e.rating),
                       count(e.id)
            )
            from EventAssessment e
            group by e.reservation.animator.id
           """)
    List<AnimatorRatingResponseDto> findAllAverageReservationRatings();
}
