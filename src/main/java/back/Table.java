package back;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;

import static back.ReservationSystem.reservations;

/**
 * The `Table` class represents a dining table at a restaurant with associated properties such as
 * table number, capacity, availability, arrival time, and leaving time.
 */

public class Table {
    @JsonProperty("tableNumber")
    private int tableNumber;

    @JsonProperty("capacity")
    private int capacity;

    @JsonProperty("available")
    private boolean isAvailable;

    /**
     * Constructs a new `Table` instance with the specified properties.
     *
     * @param tableNumber The unique identifier for the table.
     * @param capacity    The maximum number of guests the table can accommodate.
     * @throws IllegalArgumentException If table number or capacity is negative.
     */
    public Table(int tableNumber, int capacity) {
        if (tableNumber < 0 && capacity < 0) {
            throw new IllegalArgumentException("Table number and capacity cannot both be negative.");
        }
        if (tableNumber < 0) {
            throw new IllegalArgumentException("Table number cannot be negative.");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isAvailable = isAvailable(); // Set the initial availability to true (assumption)
    }

    public Table() {

    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Table " + tableNumber + " (Capacity: " + capacity + ")";
    }

    public static void updateTableAvailability(int tableNumber, String newReservationArrivalTime, String newReservationLeavingTime, String calculatedCategory) {
        for (Reservation reservation : reservations) {
            if (reservation.getTableNumber() == tableNumber) {
                LocalTime existingArrivalTime = LocalTime.parse(reservation.getArrivalTime());
                LocalTime existingLeavingTime = existingArrivalTime.plusHours(2); // Calculate leaving time as 2 hours after arrival

                LocalTime newResArrivalTime = LocalTime.parse(newReservationArrivalTime);
                LocalTime newResLeavingTime = newResArrivalTime.plusHours(2); // Calculate leaving time as 2 hours after arrival

                // Check if new reservation overlaps with an existing reservation
                if ((newResArrivalTime.isBefore(existingLeavingTime) && newResLeavingTime.isAfter(existingArrivalTime)) ||
                        (newResLeavingTime.isAfter(existingArrivalTime) && newResArrivalTime.isBefore(existingLeavingTime))) {
                    return; // New reservation overlaps with an existing reservation
                }
            }
        }
    }
}