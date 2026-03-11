package com.gladysz.kidspartymanager.repository;

import com.gladysz.kidspartymanager.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
       select r from Reservation r
       where r.status in ('NEW', 'CONFIRMED')
       """)
    List<Reservation> findActiveReservations();


    @Query("""
       select r from Reservation r
       where r.status = 'NEW' and r.createdAt < :limit
       """
    )
    List<Reservation> findExpiredNewReservations(@Param("limit") LocalDateTime limit);


    @Query("""
        select r from Reservation r
        where r.status = 'CONFIRMED' and r.eventDateTime < :now
        """
    )
    List<Reservation> findConfirmedReservationsStartedBefore(@Param("now") LocalDateTime now);

}
