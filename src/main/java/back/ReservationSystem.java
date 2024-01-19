package back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The `ReservationSystem` class manages reservations and table availability at a restaurant.
 * It provides methods for adding, removing, and updating reservations, as well as checking
 * the availability of tables and managing reservation data from JSON files.
 */

public class ReservationSystem {
   static public List<Reservation> reservations = new ArrayList<>();

    /**
     * Creates a new instance of the `ReservationSystem` class.
     * Initializes the list of reservations and reads table data from a JSON file.
     */
    public ReservationSystem() {
        reservations = readReservationDataFromJson();
    }


    public boolean isTableAvailable(int tableNumber) {
        if (reservations != null) {
            for (Reservation table : reservations) {
                if (table.getTableNumber() == tableNumber) {
                    return table.isAvailable();
                }
            }
        }
        return false; // Table not found, consider as unavailable
    }


    /**
     * Reads table data from a JSON file and initializes table availability.
     *
     * @return A list of table reservations.
     */

    private List<Reservation> readReservationDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            reservations = objectMapper.readValue(new File("src/main/resources/tables.json"), new TypeReference<>() {
            });

            // Set the availability property of each table for each reservation
            for (Reservation reservation : reservations) {
                int tableNumber = reservation.getTableNumber();
                boolean isAvailable = isTableAvailable(tableNumber);
                reservation.setAvailable(isAvailable);

                // Assign category based on table number
                if (tableNumber >= 1 && tableNumber <= 5) {
                    reservation.setCategory();
                } else if (tableNumber >= 6 && tableNumber <= 10) {
                    reservation.setCategory();
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

    public static String calculateCategory(int normalPeople, int disabilitiesPeople) {
        if (disabilitiesPeople >= normalPeople) {
            return "Special Needs";
        } else {
            return "Normal";
        }
    }

    /**
     * Checks if a new reservation with the specified arrival and departure times overlaps with any existing reservations
     * for a given table.
     *
     * @param tableNumber      The number of the table for which the reservation is being checked.
     * @param newArrivalTime   The arrival time of the new reservation.
     * @param newDepartureTime The departure time of the new reservation.
     * @return {@code true} if there is an overlap with an existing reservation, {@code false} otherwise.
     */

    public static boolean isReservationOverlapping(int tableNumber, LocalTime newArrivalTime, LocalTime newDepartureTime) {
        for (Reservation existingReservation : reservations) {
            if (existingReservation.getTableNumber() == tableNumber) {
                LocalTime existingArrival = LocalTime.parse(existingReservation.getArrivalTime());
                LocalTime existingDeparture = LocalTime.parse(existingReservation.getLeavingTime());

                if (newArrivalTime.isBefore(existingDeparture) && newDepartureTime.isAfter(existingArrival)) {
                    return true; // Conflicting reservation found
                }
            }
        }
        return false; // No conflicts found
    }

}
