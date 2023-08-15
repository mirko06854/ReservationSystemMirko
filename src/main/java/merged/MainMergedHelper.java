package merged;

import back.Reservation;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

/**
 * This interface defines helper methods for the MainMerged class, which handles reservation and table management.
 */
public interface MainMergedHelper {

    void loadReservedTables();

    /**
     * Finds the Reservation object corresponding to the selected ReservationDisplay.
     *
     * @param selectedReservation The selected reservation display.
     * @return The corresponding Reservation object, or null if not found.
     */
    Reservation findReservation(ReservationDisplay selectedReservation);

    boolean updateTableAvailability(int tableNumber, String newReservationArrivalTime, String newReservationLeavingTime, String calculatedCategory);

    void serializeJsonFile();

    void cleanJson();

    void stop();

    /**
     * Calculates the duration between two LocalTime instances.
     *
     * @param startTime The starting time.
     * @param endTime   The ending time.
     * @return A Duration object representing the duration between the two times.
     */
    Duration calculateDuration(LocalTime startTime, LocalTime endTime);

    /**
     * Determines the category of a given table number.
     *
     * @param tableNumber The table number to determine the category for.
     * @return The category of the table.
     */
    String getCategoryForTable(int tableNumber);

    boolean isReservationConflicting(Reservation newReservation);

    /**
     * Retrieves the last departure time for a given table number.
     *
     * @param tableNumber The table number to retrieve the last departure time for.
     * @return The last departure time for the table.
     */
    LocalTime getLastDepartureTimeForTable(int tableNumber);

    void validateInputValues(int tableNumber, int capacity, int people, int disabilitiesPeople);

    void validateTableNumber(int tableNumber);

    LocalTime calculateArrivalTime(String time);

    boolean isReservationOverlapping(int tableNumber, LocalTime newArrivalTime, LocalTime newDepartureTime);

    void showOverlapAlert();

    String calculateCategory(int people, int disabilitiesPeople);

    boolean validateTableCategory(int tableNumber, int capacity, String category);

    void showReservedAlert(int tableNumber, String time);

    void addReservation(String name, String time, int tableNumber, int capacity);

    void clearInputFields();

    void showInvalidNumberAlert();

    void showInvalidInputAlert(String message);
}
