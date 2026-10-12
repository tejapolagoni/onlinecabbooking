package com.cab.booking.controller;

import com.cab.booking.repository.BookingRepository;
import com.cab.booking.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final BookingRepository bookings;
    private final UserRepository users;

    public AdminController(BookingRepository bookings, UserRepository users) {
        this.bookings = bookings;
        this.users = users;
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of(
                "drivers", users.countByRole("DRIVER"),
                "acceptedBookings", bookings.countByStatus("ACCEPTED"),
                "cancelledBookings", bookings.countByStatus("CANCELLED"),
                "completedBookings", bookings.countByStatus("COMPLETED")
        );
    }
}
