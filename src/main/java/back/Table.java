package back;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Table {
    @JsonProperty("tableNumber")
    private int tableNumber;

    @JsonProperty("capacity")
    private int capacity;

    @JsonProperty("available")
    private boolean isAvailable;

    @JsonProperty("arrivalTime")
    private String arrivalTime;

    @JsonProperty("leavingTime")
    private String leavingTime;

    public Table(int tableNumber, int capacity,String arrivalTime, String leavingTime) {
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
        this.arrivalTime = arrivalTime;
        this.leavingTime = leavingTime;
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

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getLeavingTime() {
        return leavingTime;
    }

    @Override
    public String toString() {
        return "Table " + tableNumber + " (Capacity: " + capacity + ")";
    }
}