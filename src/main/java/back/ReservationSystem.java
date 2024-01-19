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

}
