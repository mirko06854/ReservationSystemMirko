package back;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {
    private List<Table> reservations;
    private List<Table> tables;

    public ReservationSystem() {
        reservations = new ArrayList<Table>();
        tables = readTableDataFromJson();
    }

    public void addReservation(Table reservation) {
        if (isTableAvailable(reservation.getTableNumber())) {
            reservations.add(reservation);
            updateTableAvailability(reservation.getTableNumber(), false);
            System.out.println("back.Reservation added: " + reservation);
        } else {
            System.out.println("back.Table not available for reservation: " + reservation.getTableNumber());
        }
    }

    public void removeReservation(Table reservation) {
        if (reservations.remove(reservation)) {
            updateTableAvailability(reservation.getTableNumber(), true);
            System.out.println("back.Reservation removed: " + reservation);
        } else {
            System.out.println("back.Reservation not found: " + reservation);
        }
    }

    public void updateReservation(Table oldReservation, Table newReservation) {
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
                return table.isAvailable();
            }
        }
        return false; // back.Table not found, consider as unavailable
    }

    private void updateTableAvailability(int tableNumber, boolean isAvailable) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.setAvailable(isAvailable);
                break;
            }
        }
    }

    private List<Table> readTableDataFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File("src/main/resources/tables.json"), new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // Return an empty list if JSON reading fails
    }
}

