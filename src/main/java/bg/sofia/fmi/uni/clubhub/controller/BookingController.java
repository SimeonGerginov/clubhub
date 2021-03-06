package bg.sofia.fmi.uni.clubhub.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bg.sofia.fmi.uni.clubhub.entity.BookingEntity.AttendanceStatus;
import bg.sofia.fmi.uni.clubhub.model.Booking;
import bg.sofia.fmi.uni.clubhub.service.BookingService;
import bg.sofia.fmi.uni.clubhub.service.IBookingService;

@RestController
@RequestMapping("customers")
public class BookingController {

    private final IBookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("{customerId}/bookings/{bookingId}")
    public ResponseEntity<Booking> getBookingForCustomer( //
            @PathVariable("bookingId") UUID bookingId, //
            @PathVariable("customerId") UUID customerId //
    ) {
        Optional<Booking> booking = bookingService.getByIdForCustomer(bookingId, customerId);
        if (!booking.isPresent()) {
            return notFound().build();
        }

        return ok(booking.get());
    }

    @GetMapping("{customerId}/bookings")
    public ResponseEntity<List<Booking>> getBookingsForCustomer( //
            @PathVariable("customerId") UUID customerId, //
            @RequestParam(value = "status", required = false) AttendanceStatus status //
    ) {
        if (status == null) {
            return ok(bookingService.getAllForCustomer(customerId));
        }

        return ok(bookingService.getAllForCustomerByStatus(customerId, status));
    }

    @PostMapping("{customerId}/bookings")
    public ResponseEntity<Booking> createBooking( //
            @PathVariable("customerId") UUID customerId, //
            @Valid @RequestBody Booking booking //
    ) {
        return status(CREATED).body(bookingService.createNew(booking));
    }

    @PutMapping("{customerId}/bookings/{bookingId}")
    public ResponseEntity cancelBooking( //
            @PathVariable("bookingId") UUID bookingID, //
            @PathVariable("customerId") UUID customerID //
    ) {
        return bookingService.cancelByIdForCustomer(bookingID, customerID) //
                .map(ResponseEntity::ok) //
                .orElse(notFound().build());
    }
}
