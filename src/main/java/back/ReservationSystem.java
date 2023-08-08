package back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {
    private List<Reservation> reservations;
    private List<Reservation> tables;

    public ReservationSystem() {
        reservations = new ArrayList<>();
        tables = readReservationDataFromJson();
    }

    public void addReservation(Reservation reservation) {
        if (isTableAvailable(reservation.getTableNumber())) {
            reservations.add(reservation);
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("Reservation added: " + reservation);
        } else {
            System.out.println("Table not available for reservation: " + reservation.getTableNumber());
        }
    }

    public void removeReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("Reservation removed: " + reservation);
        } else {
            System.out.println("Reservation not found: " + reservation);
        }
    }

    public void updateReservation(Reservation oldReservation, Reservation newReservation) {
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

    private List<Reservation> readReservationDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Reservation> reservations = objectMapper.readValue(new File("src/main/resources/tables.json"), new TypeReference<List<Reservation>>() {});

            // Set the availability property of each table for each reservation
            for (Reservation reservation : reservations) {
                int tableNumber = reservation.getTableNumber();
                boolean isAvailable = isTableAvailable(tableNumber);
                reservation.setAvailable(isAvailable);
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
}
