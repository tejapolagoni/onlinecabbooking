package com.cab.booking.controller;

import com.cab.booking.entity.Booking;
import com.cab.booking.service.BookingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping                       public Booking create(@RequestBody Booking b) { return service.create(b); }
    @GetMapping                        public List<Booking> all() { return service.all(); }
    @GetMapping("/pending")            public List<Booking> pending() { return service.pending(); }
    @GetMapping("/user/{id}")          public List<Booking> byUser(@PathVariable Long id) { return service.byUser(id); }
    @GetMapping("/driver/{id}")        public List<Booking> byDriver(@PathVariable Long id) { return service.byDriver(id); }
    @PutMapping("/{id}/cancel")        public Booking cancel(@PathVariable Long id) { return service.cancel(id); }
    @PutMapping("/{id}/accept")        public Booking accept(@PathVariable Long id, @RequestParam Long driverId) { return service.accept(id, driverId); }
    @PutMapping("/{id}/complete")      public Booking complete(@PathVariable Long id) { return service.complete(id); }
}
