package back;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import merged.MainMerged;
import merged.ReservationDisplay;

import java.util.HashMap;
import java.util.Map;

import static back.ReservationSystem.reservations;


/**
 * The `Reservation` class represents a reservation for a table at a restaurant.
 * It includes information such as the guest's name, arrival and leaving times, table details,
 * and a map of ordered plates.
 */
public class Reservation {
    private final String name;
    private final String arrivalTime;
    private final String leavingTime;
    private final Table table;

    private Map<String, Integer> platesMap;

    /* Logic for the category being setted. Since I have tables that have maximum 5 sitting places for just one category, whereas for the other tha max is 3, I want to assign random values, with a range from 1 to 5, and I add
    the restriction that the sum of the people must be <=5
     */

    private final int max = 5;
    private final int min = 1;
    int range = (max - min) + 1;
    int disabilitiesPeople = (int)(Math.random() * range) + min;
    int normalPeople = (int)(Math.random() * range) + min;

    int sumOfPeople = 5;


    /**
     * Creates a new reservation with the specified name, arrival time, table number, and capacity.
     *
     * @param name        The name of the guest making the reservation.
     * @param arrivalTime The time of arrival for the reservation.
     * @param tableNumber The number of the table reserved.
     * @param capacity    The capacity of the reserved table.
     */
    public Reservation(@JsonProperty("name") String name, @JsonProperty("arrivalTime") String arrivalTime,
                       @JsonProperty("tableNumber") int tableNumber, @JsonProperty("capacity") int capacity) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        // Calculate leaving time as 2 hours after arrival time
        this.leavingTime = calculateLeavingTime(arrivalTime);
        this.table = new Table(tableNumber, capacity);

        this.platesMap = new HashMap<>(); // Initialize the list of plates, this what the problem when the reservation's orders couldn't be attached.
    }

    public String getName() {
        return name;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getLeavingTime() {
        return leavingTime;
    }

    public int getTableNumber() {
        return table.getTableNumber();
    }

    public int getCapacity() {
        return table.getCapacity();
    }

    public boolean isAvailable() {
        return table.isAvailable();
    }

    public void setAvailable(boolean available) {
        table.setAvailable(!available);
    }

    @JsonIgnore
    public StringProperty getNameProperty() {
        return new SimpleStringProperty(name);
    }

    @JsonIgnore
    public StringProperty getArrivalTimeProperty() {
        return new SimpleStringProperty(arrivalTime);
    }

    @JsonIgnore
    public SimpleIntegerProperty getTableNumberProperty() {
        return new SimpleIntegerProperty(table.getTableNumber());
    }

    @JsonIgnore
    public SimpleIntegerProperty getCapacityProperty() {
        return new SimpleIntegerProperty(table.getCapacity());
    }

    // Getters and setters for other properties

    @JsonIgnore
    public Table getTable() {
        return table;
    }

    /**
     * Calculates the leaving time based on the arrival time.
     *
     * @param arrivalTime The time of arrival.
     * @return The calculated leaving time, 2 hours after arrival time.
     */
    private String calculateLeavingTime(String arrivalTime) {
        // Parse the arrival time to extract hours and minutes
        String[] timeParts = arrivalTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        // Calculate the departure time (2 hours later)
        int departureHours = (hours + 2) % 24; // Handle wrapping around midnight
        return String.format("%02d:%02d", departureHours, minutes);
    }


    /**
     * Sets the plates map for the reservation, indicating the ordered plates and quantities.
     *
     * @param platesMap The map of ordered plates and their quantities.
     */
    @JsonIgnore
    // Add a method to set the plates map
    public void setPlatesMap(Map<String, Integer> platesMap) {
        this.platesMap = platesMap;
    }

    /**
     * Retrieves the plates map for the reservation, indicating the ordered plates and quantities.
     *
     * @return The map of ordered plates and their quantities.
     */
    @JsonIgnore
    // Add a method to get the plates map
    public Map<String, Integer> getPlatesMap() {
        return platesMap;
    }

    /**
     * Decrements the quantity of a specific plate in the reservation's plates map.
     *
     * @param plateName The name of the plate to decrement.
     */
    public void decrementPlateQuantity(String plateName) {
        if (platesMap.containsKey(plateName)) {
            int currentQuantity = platesMap.get(plateName);
            if (currentQuantity > 0) {
                platesMap.put(plateName, currentQuantity - 1);
            }
        }
    }

    public void setCategory() {
        StringBuilder x = new StringBuilder();
        while (disabilitiesPeople + normalPeople <= sumOfPeople) {
            if (disabilitiesPeople >= normalPeople) {
                x.append("Special Needs");

            } else {
                x.append("Normal");

            }
        }
    }

    /**
     * Finds the Reservation object corresponding to the selected ReservationDisplay.
     *
     * @param selectedReservation The selected reservation display.
     * @return The corresponding Reservation object, or null if not found.
     */
    public static Reservation findReservation(ReservationDisplay selectedReservation) {
        for (Reservation reservation : reservations) {
            if (reservation.getName().equals(selectedReservation.getName().get()) &&
                    reservation.getArrivalTime().equals(selectedReservation.getTime().get()) &&
                    reservation.getTableNumber() == selectedReservation.getTableNumber().get() &&
                    reservation.getCapacity() == selectedReservation.getCapacity().get()) {
                return reservation;
            }
        }
        return null;
    }

    /**
     * Adds a new reservation with the specified details, such as guest name, arrival time, table number, and capacity.
     * Additionally, it schedules the table to be unlocked after a fixed duration from the reservation's arrival time.
     *
     * @param name        The name of the guest making the reservation.
     * @param time        The arrival time for the reservation.
     * @param tableNumber The number of the reserved table.
     * @param capacity    The capacity of the reserved table.
     */
    public static void addReservation(String name, String time, int tableNumber, int capacity) {
        Reservation reservation = new Reservation(name, time, tableNumber, capacity);
        reservations.add(reservation);
        ReservationDisplay.reservationDisplays.add(new ReservationDisplay(
                reservation.getName(),
                reservation.getArrivalTime(),
                reservation.getTableNumber(),
                reservation.getCapacity()
        ));
        back.Main.serializeJsonFile();
        MainMerged.clearInputFields();
    }

    public static void deleteEachReservation() {
        ReservationDisplay selectedReservation = MainMerged.reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Reservation reservation = Reservation.findReservation(selectedReservation);
            if (reservation != null) {
                reservations.remove(reservation);
                ReservationDisplay.reservationDisplays.remove(selectedReservation);
                Table.updateTableAvailability(reservation.getTableNumber(), reservation.getArrivalTime()); // Set the table as available again
                back.Main.serializeJsonFile(); // Save changes to the JSON file
            }
        }
    }

}
