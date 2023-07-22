package back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {
    private List<Reservation> reservations;
    private List<Table> tables;

    public ReservationSystem() {
        reservations = new ArrayList<>();
        tables = readTableDataFromJson();
    }

    public void addReservation(Reservation reservation) {
        if (isTableAvailable(reservation.getTableNumber())) {
            reservations.add(reservation);
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("back.Reservation added: " + reservation);
        } else {
            System.out.println("back.Table not available for reservation: " + reservation.getTableNumber());
        }
    }

    public void removeReservation(Reservation reservation) {
        if (reservations.remove(reservation)) {
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("back.Reservation removed: " + reservation);
        } else {
            System.out.println("back.Reservation not found: " + reservation);
        }
    }

    public void updateReservation(Reservation oldReservation, Reservation newReservation) {
        if (reservations.contains(oldReservation)) {
            if (isTableAvailable(newReservation.getTableNumber())) {
                reservations.remove(oldReservation);
                reservations.add(newReservation);
                updateTableAvailability(oldReservation.getTableNumber(), true);
                updateTableAvailability(newReservation.getTableNumber(), false);
                System.out.println("back.Reservation updated: " + oldReservation + " -> " + newReservation);
            } else {
                System.out.println("back.Table not available for reservation: " + newReservation.getTableNumber());
            }
        } else {
            System.out.println("back.Reservation not found: " + oldReservation);
        }
    }

    public boolean isTableAvailable(int tableNumber) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return false;
            }
        }
        return true; // back.Table not found, consider as available
    }

    private void updateTableAvailability(int tableNumber, boolean isAvailable) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.setAvailable(false);
                break;
            }
        }
    }
    private List<Table> readTableDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Table> tables = objectMapper.readValue(new File("src/main/resources/tables.json"), new TypeReference<List<Table>>() {});

            // Set the isAvailable property of each table to false
            for (Table table : tables) {
                int tableNumber = table.getTableNumber();
                boolean isAvailable = isTableAvailable(tableNumber);
                table.setAvailable(isAvailable);
            }

            return tables;
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
