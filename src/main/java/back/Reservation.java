package back;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** A class representing reservations, since the table may be reserved we thought to make this class being extended by Table, since is each table instance to be reserved
 *
 */
public class Reservation extends Table {
    private final String name;
    private final String time;

    public Reservation() {
        super(0, 0); // Initialize tableNumber and capacity with default values
        this.name = null;
        this.time = null;
    }


    public Reservation(String name, String time, int tableNumber, int capacity) {
        super(tableNumber, capacity);
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }


    @Override
    public int getTableNumber() {
        return super.getTableNumber();
    }


    @Override
    public int getCapacity() {
        return super.getCapacity();
    }

    // json ignore allow to omit some unwished information in the JSON retrieved in src/main/resources/tables.json
    @JsonIgnore
    public StringProperty getNameProperty() {
        return new SimpleStringProperty(name);
    }

    @JsonIgnore
    public StringProperty getTimeProperty() {
        return new SimpleStringProperty(time);
    }

    @JsonIgnore
    public SimpleIntegerProperty getTableNumberProperty() {
        return new SimpleIntegerProperty(super.getTableNumber());
    }

    @JsonIgnore
    public SimpleIntegerProperty getCapacityProperty() {
        return new SimpleIntegerProperty(super.getCapacity());
    }
}
