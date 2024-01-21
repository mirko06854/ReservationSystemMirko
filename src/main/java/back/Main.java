package back;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import merged.ReservationDisplay;

import java.io.File;
import java.io.IOException;

import static back.ReservationSystem.reservations;

/**
 * This class is the main class.
 * We have an <b>ObjectMapper</b>
 * that is used for serialize and deserialize Json to back.Table Object
 * and vice-versa
 **/
public class Main {

    public static ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {

        // Create a new back.Table instance
        Table table = new Table(3, 4);

        try {
            // Serialize the Table object to JSON and write it to a file
            objectMapper.writeValue(new File("src/main/resources/tables.json"), table);

            // Deserialize the JSON from the file back into a Table object
            Table deserializedTable = objectMapper.readValue(new File("src/main/resources/tables.json"), Table.class);

            // Print the deserialized Table object
            System.out.println("Deserialized back.Table: " + deserializedTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeJsonFile() {
        File file = new File("src/main/resources/tables.json");
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads reservations from a JSON file and populates the application's data structures.
     * If the file exists, it reads the reservations and creates corresponding displays.
     * Reservations and their displays are added to internal collections.
     */
    public static void loadReservedTables() {
        try {
            File file = new File("src/main/resources/tables.json");
            if (file.exists()) {
                Reservation[] loadedTables = back.Main.objectMapper.readValue(file, Reservation[].class);

                for (Reservation reservation : loadedTables) {
                    ReservationDisplay display = new ReservationDisplay(
                            reservation.getName(),
                            reservation.getArrivalTime(),
                            reservation.getTableNumber(),
                            reservation.getCapacity()
                    );
                    reservations.add(reservation);
                    ReservationDisplay.reservationDisplays.add(display);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}