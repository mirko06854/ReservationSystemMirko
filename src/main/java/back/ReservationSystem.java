package back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The `ReservationSystem` class manages reservations and table availability at a restaurant.
 * It provides methods for adding, removing, and updating reservations, as well as checking
 * the availability of tables and managing reservation data from JSON files.
 */

public class ReservationSystem {
    private List<Reservation> reservations;
    private List<Reservation> tables;

    /**
     * Creates a new instance of the `ReservationSystem` class.
     * Initializes the list of reservations and reads table data from a JSON file.
     */
    public ReservationSystem() {
        reservations = new ArrayList<>();
        tables = readReservationDataFromJson();
    }

    /**
     * Adds a reservation to the system, if the table is available.
     *
     * @param reservation The reservation to be added.
     */
    private void addReservation(Reservation reservation) {
        if (isTableAvailable(reservation.getTableNumber())) {
            reservations.add(reservation);
            updateTableAvailability(reservation.getTableNumber(), false);
            System.out.println("Reservation added: " + reservation);
        } else {
            System.out.println("Table not available for reservation: " + reservation.getTableNumber());
        }
    }

    /**
     * Creates a new reservation with the specified details and adds it to the system, if the table is available.
     *
     * @param name               The name of the guest.
     * @param time               The arrival time.
     * @param tableNumber        The table number.
     * @param capacity           The table capacity.
     * @param totalPeople        The total number of people.
     * @param disabilitiesPeople The number of people with disabilities.
     * @return The created reservation.
     * @throws IllegalArgumentException If the table is not available for reservation.
     */
    private Reservation createReservation(String name, String time, int tableNumber, int capacity, int totalPeople, int disabilitiesPeople) {
        // Validate inputs, calculate category, and create reservation
        String category = calculateCategory(totalPeople, disabilitiesPeople);
        Reservation reservation = new Reservation(name, time, tableNumber, capacity);
        reservation.setCategory(category);

        // Check table availability and add reservation
        if (isTableAvailable(tableNumber)) {
            reservations.add(reservation);
            updateTableAvailability(tableNumber, false); // Table is now reserved
            return reservation;
        } else {
            throw new IllegalArgumentException("Table " + tableNumber + " is not available for reservation.");
        }
    }


    private void removeReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("Reservation removed: " + reservation);
        } else {
            System.out.println("Reservation not found: " + reservation);
        }
    }

    private void updateReservation(Reservation oldReservation, Reservation newReservation) {
        if (reservations.contains(oldReservation)) {
            if (!isTableReserved(newReservation.getTableNumber(), newReservation.getArrivalTime(), newReservation.getLeavingTime())) {
                reservations.remove(oldReservation);
                reservations.add(newReservation);
                updateTableAvailability(oldReservation.getTableNumber(), true);
                updateTableAvailability(newReservation.getTableNumber(), false);
                System.out.println("Reservation updated: " + oldReservation + " -> " + newReservation);
            } else {
                System.out.println("Table already reserved at the given time: " + newReservation.getTableNumber());
            }
        } else {
            System.out.println("Reservation not found: " + oldReservation);
        }
    }

    public boolean isTableAvailable(int tableNumber) {
        if (tables != null) {
            for (Reservation table : tables) {
                if (table.getTableNumber() == tableNumber) {
                    return table.isAvailable();
                }
            }
        }
        return false; // Table not found, consider as unavailable
    }

    public boolean isTableReserved(int tableNumber, String arrivalTime, String leavingTime) {
        for (Reservation reservation : reservations) {
            if (reservation.getTableNumber() == tableNumber) {
                // Check for overlapping reservations
                if (!reservation.isAvailable()) {
                    String existingArrivalTime = reservation.getTable().getArrivalTime();
                    String existingLeavingTime = reservation.getTable().getLeavingTime();

                    // Check if the existing reservation overlaps with the new reservation's time range
                    if (arrivalTime.compareTo(existingLeavingTime) < 0 && leavingTime.compareTo(existingArrivalTime) > 0) {
                        return true; // Table is already reserved at an overlapping time
                    }
                }
            }
        }
        return false; // Table is not reserved at the given time range
    }


    private void updateTableAvailability(int tableNumber, boolean isAvailable) {
        for (Reservation table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.setAvailable(isAvailable);
                break;
            }
        }
    }

    /**
     * Reads table data from a JSON file and initializes table availability.
     *
     * @return A list of table reservations.
     */

    private List<Reservation> readReservationDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Reservation> reservations = objectMapper.readValue(new File("src/main/resources/tables.json"), new TypeReference<List<Reservation>>() {
            });

            // Set the availability property of each table for each reservation
            for (Reservation reservation : reservations) {
                int tableNumber = reservation.getTableNumber();
                boolean isAvailable = isTableAvailable(tableNumber);
                reservation.setAvailable(isAvailable);

                // Assign category based on table number
                if (tableNumber >= 1 && tableNumber <= 5) {
                    reservation.setCategory("Normal");
                } else if (tableNumber >= 6 && tableNumber <= 10) {
                    reservation.setCategory("Special Needs");
                }
            }

            return reservations;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // Return an empty list if JSON reading fails
    }

    public static void validateTableBookingTime(String time) {
        if (!time.matches("^([01]?[0-9]|2[0-4]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Invalid table booking time: " + time);
        }
    }

    public String calculateCategory(int normalPeople, int disabilitiesPeople) {
        if (disabilitiesPeople >= normalPeople) {
            return "Special Needs";
        } else {
            return "Normal";
        }
    }

}
