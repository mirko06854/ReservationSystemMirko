package merged;

import javafx.beans.property.*;

/**
 * Since the Reservation class extends the Table class, this caused issues when using JavaFX's TableView, infact I couldn't see the content of table inserted in the GUI.
 * Therefore, to manage this issue, I created this separate class for displaying reservations in the table.
 **/

public class ReservationDisplay {
    private StringProperty name;
    private StringProperty time;
    private IntegerProperty tableNumber;
    private IntegerProperty capacity;
    private IntegerProperty remainingTime;

    public ReservationDisplay(String name, String time, int tableNumber, int capacity) {
        this.name = new SimpleStringProperty(name);
        this.time = new SimpleStringProperty(time);
        this.tableNumber = new SimpleIntegerProperty(tableNumber);
        this.capacity = new SimpleIntegerProperty(capacity);
        this.remainingTime = new SimpleIntegerProperty(); // remaining time
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty timeProperty() {
        return time;
    }

    public IntegerProperty tableNumberProperty() {
        return tableNumber;
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    // Add getter methods for the private fields
    public StringProperty getName() {
        return name;
    }

    public StringProperty getTime() {
        return time;
    }

    public IntegerProperty getTableNumber() {
        return tableNumber;
    }

    public IntegerProperty getCapacity() {
        return capacity;
    }

    public int getRemainingTime() {
        return remainingTime.get();
    }

    public IntegerProperty remainingTimeProperty() {
        return remainingTime;
    }

    public void setRemainingTime(int minutes) {
        remainingTime.set(minutes);
    }
}