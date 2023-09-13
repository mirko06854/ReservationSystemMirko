package back;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The `Reservation` class represents a reservation for a table at a restaurant.
 * It includes information such as the guest's name, arrival and leaving times, table details,
 * and a map of ordered plates.
 */
public class Reservation {
    private String name;
    private String arrivalTime;
    private String leavingTime;
    private Table table;

    private String category;

    private LocalTime unlockTime; // New field for unlock time
    private boolean locked; // New field for locked status

    private Map<String, Integer> platesMap;

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
        this.table = new Table(tableNumber, capacity, arrivalTime, leavingTime);

        // Calculate unlock time as 1 minute after leaving time (adjust as needed)
        this.unlockTime = LocalTime.parse(leavingTime).plusMinutes(1);
        this.locked = true; // Mark reservation as locked initially
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
     * Sets the category of the reservation.
     *
     * @param category The category to set for the reservation.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonIgnore
    public LocalTime getUnlockTime() {
        return unlockTime;
    }

    @JsonIgnore
    public boolean isLocked() {
        return locked;
    }

    @JsonIgnore
    public void setLocked(boolean locked) {
        this.locked = locked;
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
     * @param i         The quantity to decrement by.
     */
    public void decrementPlateQuantity(String plateName, int i) {
        if (platesMap.containsKey(plateName)) {
            int currentQuantity = platesMap.get(plateName);
            if (currentQuantity > 0) {
                platesMap.put(plateName, currentQuantity - 1);
            }
        }
    }

}
