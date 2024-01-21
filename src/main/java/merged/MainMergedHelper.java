package merged;

import back.Reservation;
import javafx.util.Duration;
import java.time.LocalTime;

/**
 * This interface defines helper methods for the MainMerged class, which handles reservation and table management.
 */
public interface MainMergedHelper {

    /**
     * Clears all reservations and their displays, refreshes the reservation table display,
     * clears input fields, and saves the changes to the JSON file.
     */
    void cleanJsonAndReservations();

    /**
     * Calculates the duration between two LocalTime instances.
     *
     * @param startTime The starting time.
     * @param endTime   The ending time.
     * @return A Duration object representing the duration between the two times.
     */
    Duration calculateDuration(LocalTime startTime, LocalTime endTime);

    boolean validateInput(int tableNumber, int people, int disabilitiesPeople, String category, String time, int totalPeople);

    void validateTableNumber(int tableNumber);

    LocalTime calculateArrivalTime(String time);

    /* tell the user when an overlaps happen*/
    void showOverlapAlert();

    /**
     * Calculates the category of a reservation based on the number of normal and disabilities guests.
     * Reservations with more disabilities guests than normal guests are categorized as "Special Needs,"
     * while others are categorized as "Normal."
     *
     * @param people             The number of normal guests.
     * @param disabilitiesPeople The number of guests with special needs or disabilities.
     * @return The category of the reservation, either "Normal" or "Special Needs."
     */
    String calculateCategory(int people, int disabilitiesPeople);

    boolean validateTableCategory(int tableNumber, int capacity, String category);

    void showInvalidNumberAlert();

    void showInvalidInputAlert(String message);

    /**
     * Displays an information dialog showing the ordered plates and their quantities for a reservation.
     *
     * @param reservation         The reservation for which to display ordered plates.
     * @param selectedReservation The selected reservation display in the GUI.
     */
    void showOrderedFoodDialog(Reservation reservation, ReservationDisplay selectedReservation);

    /**
     * Opens a dialog for selecting dishes and quantities to add to a reservation.
     *
     * @param reservation The reservation to which dishes and quantities will be added.
     */
    void openDishesPopup(Reservation reservation);

}
