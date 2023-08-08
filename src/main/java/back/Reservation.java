package back;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class Reservation {
    private String name;
    private String arrivalTime;
    private String leavingTime;
    private Table table;

    public Reservation(@JsonProperty("name") String name,  @JsonProperty("arrivalTime") String arrivalTime,
                       @JsonProperty("tableNumber") int tableNumber, @JsonProperty("capacity") int capacity) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        // Calculate leaving time as 2 hours after arrival time
        this.leavingTime = calculateLeavingTime(arrivalTime);
        this.table = new Table(tableNumber, capacity, arrivalTime, leavingTime);
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
        table.setAvailable(available);
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
    public StringProperty getLeavingTimeProperty() {
        return new SimpleStringProperty(leavingTime);
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
    private String calculateLeavingTime(String arrivalTime) {
        // Parse the arrival time to extract hours and minutes
        String[] timeParts = arrivalTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);

        // Calculate the departure time (2 hours later)
        int departureHours = (hours + 2) % 24; // Handle wrapping around midnight
        return String.format("%02d:%02d", departureHours, minutes);
    }
}
