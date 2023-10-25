package back;

import com.fasterxml.jackson.annotation.JsonProperty;

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
     * @param arrivalTime The time when guests can arrive at the table.
     * @param leavingTime The time when guests must leave the table.
     * @throws IllegalArgumentException If table number or capacity is negative.
     */
    public Table(int tableNumber, int capacity, String arrivalTime, String leavingTime) {
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
}