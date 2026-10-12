package com.cab.booking.service;

import com.cab.booking.entity.Booking;
import com.cab.booking.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class BookingService {
    private final BookingRepository repo;
    private final Random random = new Random();

    public BookingService(BookingRepository repo) {
        this.repo = repo;
    }

    public Booking create(Booking b) {
        int km = 3 + random.nextInt(18);          // fake distance 3-20 km
        b.setDistanceKm(km);
        b.setFare(50 + 12.0 * km);                // base 50 + 12 per km
        b.setStatus("PENDING");
        b.setBookingTime(LocalDateTime.now());
        return repo.save(b);
    }

    public List<Booking> byUser(Long id) { return repo.findByUserIdOrderByIdDesc(id); }
    public List<Booking> byDriver(Long id) { return repo.findByDriverIdOrderByIdDesc(id); }
    public List<Booking> pending() { return repo.findByStatusOrderByIdDesc("PENDING"); }
    public List<Booking> all() { return repo.findAll(); }

    public Booking cancel(Long id) {
        Booking b = get(id);
        if (!b.getStatus().equals("PENDING") && !b.getStatus().equals("ACCEPTED"))
            throw bad("Only pending or accepted bookings can be cancelled");
        b.setStatus("CANCELLED");
        return repo.save(b);
    }

    public Booking accept(Long id, Long driverId) {
        Booking b = get(id);
        if (!b.getStatus().equals("PENDING")) throw bad("Booking is no longer available");
        b.setDriverId(driverId);
        b.setStatus("ACCEPTED");
        return repo.save(b);
    }

    public Booking complete(Long id) {
        Booking b = get(id);
        if (!b.getStatus().equals("ACCEPTED")) throw bad("Only accepted rides can be completed");
        b.setStatus("COMPLETED");
        return repo.save(b);
    }

    private Booking get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
