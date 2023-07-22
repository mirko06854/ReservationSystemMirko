package back;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Table {
    @JsonProperty("tableNumber")
    private int tableNumber;

    @JsonProperty("capacity")
    private int capacity;

    @JsonProperty("isAvailable") // Add this annotation to include the property in JSON serialization
    private boolean isAvailable;

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
        this.isAvailable = true; // Set the initial availability to true (assumption)
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