package back;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** A class representing reservations */
public class Reservation {
    private final String name;
    private final String time;

    private Table table;

    @JsonCreator
    public Reservation(@JsonProperty("name") String name, @JsonProperty("time") String time,
                       @JsonProperty("tableNumber") int tableNumber, @JsonProperty("capacity") int capacity) {
        this.name = name;
        this.time = time;
        this.table = new Table(tableNumber, capacity);
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
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

    @JsonIgnore // Ignore these properties during JSON serialization
    public StringProperty getNameProperty() {
        return new SimpleStringProperty(name);
    }

    @JsonIgnore
    public StringProperty getTimeProperty() {
        return new SimpleStringProperty(time);
    }

    @JsonIgnore
    public SimpleIntegerProperty getTableNumberProperty() {
        return new SimpleIntegerProperty(table.getTableNumber());
    }

    @JsonIgnore
    public SimpleIntegerProperty getCapacityProperty() {
        return new SimpleIntegerProperty(table.getCapacity());
    }


    @JsonIgnore
    public Table getTable() {
        return table;
    }
}