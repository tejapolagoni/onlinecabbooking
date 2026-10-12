package com.cab.booking.repository;

import com.cab.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByIdDesc(Long userId);
    List<Booking> findByDriverIdOrderByIdDesc(Long driverId);
    List<Booking> findByStatusOrderByIdDesc(String status);
    long countByStatus(String status);
}
