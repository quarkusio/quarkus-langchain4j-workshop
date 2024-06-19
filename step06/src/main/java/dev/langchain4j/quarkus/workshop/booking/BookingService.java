package dev.langchain4j.quarkus.workshop.booking;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BookingService {

    private List<Booking> bookings = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        // can't be cancelled because it is shorter than 4 days
        Booking booking1 = new Booking("123-456",
                LocalDate.now().plusDays(17),
                LocalDate.now().plusDays(19),
                new Customer("Klaus", "Heisler"));
        bookings.add(booking1);
        // can't be cancelled because it starts in less than 11 days
        Booking booking2 = new Booking("111-111",
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(8),
                new Customer("David", "Wood"));
        bookings.add(booking2);
        // can be cancelled
        Booking booking3 = new Booking("222-222",
                LocalDate.now().plusDays(12),
                LocalDate.now().plusDays(21),
                new Customer("Martin", "Oak"));
        bookings.add(booking3);
    }

    public void cancelBooking(String bookingNumber, String customerName, String customerSurname) {
        Booking booking = getBookingDetails(bookingNumber, customerName, customerSurname);
        // too late to cancel
        if(booking.dateFrom().minusDays(11).isBefore(LocalDate.now())) {
            throw new BookingCannotBeCancelledException(bookingNumber);
        }
        // too short to cancel
        if(booking.dateTo().minusDays(4).isBefore(booking.dateFrom())) {
            throw new BookingCannotBeCancelledException(bookingNumber);
        }
        bookings.remove(booking);
    }

    public Booking getBookingDetails(String bookingNumber, String customerName, String customerSurname) {
        return bookings.stream()
                .filter(booking -> booking.bookingNumber().equals(bookingNumber))
                .filter(booking -> booking.customer().name().equals(customerName))
                .filter(booking -> booking.customer().surname().equals(customerSurname))
                .findAny()
                .orElseThrow(() -> new BookingNotFoundException(bookingNumber));
    }
}
