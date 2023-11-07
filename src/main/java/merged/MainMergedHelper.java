package merged;

import back.Reservation;
import javafx.util.Duration;
import java.time.LocalTime;

/**
 * This interface defines helper methods for the MainMerged class, which handles reservation and table management.
 */
public interface MainMergedHelper {

    /**
     * Loads reservations from a JSON file and populates the application's data structures.
     * If the file exists, it reads the reservations and creates corresponding displays.
     * Reservations and their displays are added to internal collections.
     */
    void loadReservedTables();

    /**
     * Finds the Reservation object corresponding to the selected ReservationDisplay.
     *
     * @param selectedReservation The selected reservation display.
     * @return The corresponding Reservation object, or null if not found.
     */
    Reservation findReservation(ReservationDisplay selectedReservation);

    void updateTableAvailability(int tableNumber, String newReservationArrivalTime, String newReservationLeavingTime, String calculatedCategory);

    void serializeJsonFile();

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

    /**
     * Determines the category of a given table number.
     *
     * @param tableNumber The table number to determine the category for.
     * @return The category of the table.
     */
    String getCategoryForTable(int tableNumber);

    // boolean isReservationConflicting(Reservation newReservation);

    void validateInputValues(int tableNumber, int capacity, int people, int disabilitiesPeople);

    void validateTableNumber(int tableNumber);

    LocalTime calculateArrivalTime(String time);

    /**
     * Checks if a new reservation with the specified arrival and departure times overlaps with any existing reservations
     * for a given table.
     *
     * @param tableNumber      The number of the table for which the reservation is being checked.
     * @param newArrivalTime   The arrival time of the new reservation.
     * @param newDepartureTime The departure time of the new reservation.
     * @return {@code true} if there is an overlap with an existing reservation, {@code false} otherwise.
     */
    boolean isReservationOverlapping(int tableNumber, LocalTime newArrivalTime, LocalTime newDepartureTime);


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

    /**
     * Adds a new reservation with the specified details, such as guest name, arrival time, table number, and capacity.
     * Additionally, it schedules the table to be unlocked after a fixed duration from the reservation's arrival time.
     *
     * @param name        The name of the guest making the reservation.
     * @param time        The arrival time for the reservation.
     * @param tableNumber The number of the reserved table.
     * @param capacity    The capacity of the reserved table.
     */
    void addReservation(String name, String time, int tableNumber, int capacity);

    void clearInputFields();

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
     * Helper method to check if all ordered plates in a reservation have been paid.
     *
     * @param reservation The reservation for which to check plate payments.
     * @return True if all plates have been paid; otherwise, false.
     */
    boolean areAllPlatesPaid(Reservation reservation);

    /**
     * Opens a dialog for selecting dishes and quantities to add to a reservation.
     *
     * @param reservation The reservation to which dishes and quantities will be added.
     */
    void openDishesPopup(Reservation reservation);

}
