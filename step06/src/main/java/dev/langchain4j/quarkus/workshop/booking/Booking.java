package dev.langchain4j.quarkus.workshop.booking;

import java.time.LocalDate;

public record Booking(String bookingNumber,
                      LocalDate dateFrom,
                      LocalDate dateTo,
                      Customer customer) {

}