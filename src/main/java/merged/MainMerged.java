package merged;

import back.Reservation;
import back.ReservationSystem;
import back.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainMerged extends Application implements MainMergedHelper {
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ObservableList<ReservationDisplay> reservationDisplays = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<Table, PauseTransition> tableTimers = new HashMap<Table, PauseTransition>(); // mapping each table with its timeline

    private TableView<ReservationDisplay> reservationTable; // Declare the reservationTable variable here

    private ReservationSystem reservationSystem = new ReservationSystem();

    Label nameLabel = new Label("Name:");
    TextField nameField = new TextField();

    Label timeLabel = new Label("Time:");
    TextField timeField = new TextField();

    Label tableNumberLabel = new Label("Table Number:");
    TextField tableNumberField = new TextField();

    Label capacityLabel = new Label("Capacity:");
    TextField capacityField = new TextField();

    Label peopleNumberLabel = new Label("people:");
    TextField peopleNumberField = new TextField();

    Label disabilitiesPeopleNumberLabel = new Label("people with disabilities:");
    TextField disabilitiesPeopleNumberField = new TextField();

    Button reserveButton = new Button("Reserve");
    Button cleanButton = new Button("Clean JSON");
    Button deleteButton = new Button("Delete Reservation");


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Reservation System");

        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        reserveButton.setOnAction(e -> {
            try {

                // Get input values
                String name = nameField.getText();
                String time = timeField.getText();
                int tableNumber = Integer.parseInt(tableNumberField.getText());
                int capacity = Integer.parseInt(capacityField.getText());
                int people = Integer.parseInt(peopleNumberField.getText());
                int disabilitiesPeople = Integer.parseInt(disabilitiesPeopleNumberField.getText());
                int totalPeople = people + disabilitiesPeople;

                //calculates the category:
                String category = calculateCategory(people, disabilitiesPeople);

                // Validate input values
                validateInputValues(tableNumber, capacity, people, disabilitiesPeople);

                if (tableNumber < 1 || capacity < 0 || people < 0 || disabilitiesPeople < 0) {
                    showInvalidInputAlert("enter other positive values ");
                    return; // Exit the method without proceeding further
                }

                if (tableNumber > 10) {
                    showInvalidInputAlert("enter table number < 11");
                    return; // Exit the method without proceeding further
                }

                if (category.equals("Normal") && (capacity > 5 || capacity < totalPeople)) {
                    showInvalidInputAlert("For tables with category 'Normal', the capacity should be the sum of people , and that must be max 5");
                    return; // Exit the method without proceeding further
                }

                if (category.equals("Special Needs") && (capacity < 3 || capacity < totalPeople)) {
                    showInvalidInputAlert("For tables with category 'Special Needs', the capacity should be the sum of people , and that must be max 3");
                    return; // Exit the method without proceeding further
                }

                // Check if the sum of people is greater than 3 and at least 3 are people with disabilities
                if (totalPeople > 3 && disabilitiesPeople >= 3) {
                    category = "Normal"; // Set category to "Normal" in this special case
                }


                // Check table number validity
                validateTableNumber(tableNumber);

                // Validate table booking time
                ReservationSystem.validateTableBookingTime(time);

                // Get the most recent departure time for the selected table
                LocalTime lastDepartureTime = getLastDepartureTimeForTable(tableNumber);

                // Calculate new arrival and departure times
                LocalTime newArrivalTime = calculateArrivalTime(time);
                LocalTime newDepartureTime = newArrivalTime.plusHours(2);

                // Check if new reservation overlaps with existing reservations

                /* Note:  compare the new reservation's arrival time with the unlock time of the most recent reservation
                for the same table. If the new reservation's arrival time is after this unlock time, then there
                shouldn't be any overlap, and you can proceed to add the reservation. So I added the logic and to fix unexpected warnings !
                 MOVED!!! */

                // Calculate the unlock time based on the last departure time
                LocalTime unlockTime = lastDepartureTime.plusMinutes(1); // Add a small buffer

                // Check if new reservation overlaps with existing reservations
                if (isReservationOverlapping(tableNumber, newArrivalTime, newDepartureTime)) {
                    if (!newArrivalTime.isAfter(lastDepartureTime.plusHours(2))) {
                        // Overlaps with the unlock time of the last reservation, but we'll still allow it
                        // Instead of showing the alert, you can just continue without adding the reservation
                        return; // Exit the method without adding the reservation
                    } else {
                        showOverlapAlert();
                        return; // Exit the method without adding the reservation
                    }
                }

                // validates the category and check if it matches the table
                validateTableCategory(tableNumber, capacity, category);

                // Validate category and capacity
                if (!validateTableCategory(tableNumber, capacity, category)) {
                    return; // Exit the method without adding the reservation
                }

                // Check if the table is already reserved during the booking time and departure time
                if (isTableReserved(tableNumber, time, newDepartureTime.format(DateTimeFormatter.ofPattern("HH:mm")))) {
                    showReservedAlert(tableNumber, time);
                    return; // Exit the method without adding the reservation
                }

                // Create and store the reservation
                addReservation(name, time, tableNumber, capacity);

                // Clear input fields
                clearInputFields();

            } catch (NumberFormatException ex) {
                showInvalidNumberAlert();
            } catch (IllegalArgumentException ex) {
                showInvalidInputAlert(ex.getMessage());
            }
        });

        cleanButton.setOnAction(e -> {
            // Cancel and remove associated PauseTransitions
            for (Reservation reservation : reservations) {
                PauseTransition tableTransition = tableTimers.get(reservation.getTable());
                if (tableTransition != null) {
                    tableTransition.stop();
                    tableTimers.remove(reservation.getTable());
                }
            }

            // Clear reservations and reservationDisplays
            reservations.clear();
            reservationDisplays.clear();

            // Clear table data and refresh display
            reservationTable.getItems().clear();
            reservationTable.refresh();

            // Clear input fields
            clearInputFields();

            // Serialize and save changes to the JSON file
            serializeJsonFile();
        });


        deleteButton.setOnAction(e -> {
            ReservationDisplay selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null) {
                Reservation reservation = findReservation(selectedReservation);
                if (reservation != null) {
                    reservations.remove(reservation);
                    reservationDisplays.remove(selectedReservation);

                    // Cancel the associated PauseTransition (if exists)
                    PauseTransition tableTransition = tableTimers.get(reservation.getTable());
                    if (tableTransition != null) {
                        tableTransition.stop();
                        tableTimers.remove(reservation.getTable());
                    }
                    updateTableAvailability(reservation.getTableNumber(), reservation.getArrivalTime(), reservation.getLeavingTime(), getCategoryForTable(reservation.getTableNumber())); // Set the table as available again
                    serializeJsonFile(); // Save changes to the JSON file
                }
            }
        });

        loadReservedTables();

        TableColumn<ReservationDisplay, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ReservationDisplay, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<ReservationDisplay, Integer> tableNumberColumn = new TableColumn<>("Table Number");
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));

        TableColumn<ReservationDisplay, Integer> capacityColumn = new TableColumn<>("Capacity");
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        reservationTable = new TableView<>();
        reservationTable.setId("reservationTable");
        reservationTable.getColumns().addAll(nameColumn, timeColumn, tableNumberColumn, capacityColumn);
        reservationTable.setItems(reservationDisplays);

        layout = new VBox(10);
        layout.getChildren().addAll(nameLabel, nameField, timeLabel, timeField, tableNumberLabel, tableNumberField, capacityLabel, capacityField, peopleNumberLabel, peopleNumberField,
                disabilitiesPeopleNumberLabel, disabilitiesPeopleNumberField, reserveButton, deleteButton, cleanButton, reservationTable);
        layout.setPadding(new Insets(10));
        scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();


        // Set the FX IDs for the UI elements
        nameField.setId("nameField");
        timeField.setId("timeField");
        tableNumberField.setId("tableNumberField");
        capacityField.setId("capacityField");
        reserveButton.setId("reserveButton");
        cleanButton.setId("cleanButton");
        deleteButton.setId("deleteButton");
        reservationTable.setId("reservationTable");
    }

    public boolean isTableReserved(int tableNumber, String arrivalTime, String leavingTime) {
        return reservationSystem.isTableReserved(tableNumber, arrivalTime, leavingTime);
    }

    public void loadReservedTables() {
        try {
            File file = new File("src/main/resources/tables.json");
            if (file.exists()) {
                Reservation[] loadedTables = objectMapper.readValue(file, Reservation[].class);

                for (Reservation reservation : loadedTables) {
                    ReservationDisplay display = new ReservationDisplay(
                            reservation.getName(),
                            reservation.getArrivalTime(),
                            reservation.getTableNumber(),
                            reservation.getCapacity()
                    );
                    reservations.add(reservation);
                    reservationDisplays.add(display);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Reservation findReservation(ReservationDisplay selectedReservation) {
        for (Reservation reservation : reservations) {
            if (reservation.getName().equals(selectedReservation.getName().get()) &&
                    reservation.getArrivalTime().equals(selectedReservation.getTime().get()) &&
                    reservation.getTableNumber() == selectedReservation.getTableNumber().get() &&
                    reservation.getCapacity() == selectedReservation.getCapacity().get()) {
                return reservation;
            }
        }
        return null;
    }

    public boolean updateTableAvailability(int tableNumber, String newReservationArrivalTime, String newReservationLeavingTime, String calculatedCategory) {
        for (Reservation reservation : reservations) {
            if (reservation.getTableNumber() == tableNumber) {
                LocalTime existingArrivalTime = LocalTime.parse(reservation.getArrivalTime());
                LocalTime existingLeavingTime = existingArrivalTime.plusHours(2); // Calculate leaving time as 2 hours after arrival

                LocalTime newResArrivalTime = LocalTime.parse(newReservationArrivalTime);
                LocalTime newResLeavingTime = newResArrivalTime.plusHours(2); // Calculate leaving time as 2 hours after arrival

                // Check if new reservation overlaps with an existing reservation
                if ((newResArrivalTime.isBefore(existingLeavingTime) && newResLeavingTime.isAfter(existingArrivalTime)) ||
                        (newResLeavingTime.isAfter(existingArrivalTime) && newResArrivalTime.isBefore(existingLeavingTime))) {
                    return false; // New reservation overlaps with an existing reservation
                }
            }
        }
        return true; // Table is available
    }

    public void serializeJsonFile() {
        File file = new File("src/main/resources/tables.json");
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanJson() {
        reservations.clear();
        reservationDisplays.clear();
        serializeJsonFile();
    }

    @Override
    public void stop() {
        // Save changes to the JSON file when the application is closed
        serializeJsonFile();
    }

    public Duration calculateDuration(LocalTime startTime, LocalTime endTime) {
        long startSeconds = startTime.toSecondOfDay();   // "toSecondOfDay()" returns the number of seconds since midnight, which eliminates any timezone-related concerns
        long endSeconds = endTime.toSecondOfDay();
        long durationSeconds = endSeconds - startSeconds;

        return new Duration(durationSeconds * 1000);   // because javafx.util.Duration uses such unit
    }

    public String getCategoryForTable(int tableNumber) {
        if (tableNumber >= 1 && tableNumber <= 5) {
            return "Normal";
        } else if (tableNumber >= 6 && tableNumber <= 10) {
            return "Special Needs";
        } else {
            return "Unknown"; // Or whatever default category you want for invalid table numbers
        }
    }

    public boolean isReservationConflicting(Reservation newReservation) {
        for (Reservation existingReservation : reservations) {
            if (existingReservation.getTableNumber() == newReservation.getTableNumber()) {
                LocalTime existingArrival = LocalTime.parse(existingReservation.getArrivalTime());
                LocalTime existingDeparture = LocalTime.parse(existingReservation.getLeavingTime());

                LocalTime newArrival = LocalTime.parse(newReservation.getArrivalTime());
                LocalTime newDeparture = LocalTime.parse(newReservation.getLeavingTime());

                if (newArrival.isBefore(existingDeparture) && newDeparture.isAfter(existingArrival)) {
                    return true; // Conflicting reservation found
                }
            }
        }
        return false; // No conflicts found
    }

    public LocalTime getLastDepartureTimeForTable(int tableNumber) {
        LocalTime lastDepartureTime = LocalTime.MIN;
        for (Reservation reservation : reservations) {
            if (reservation.getTableNumber() == tableNumber) {
                LocalTime departureTime = LocalTime.parse(reservation.getLeavingTime());
                if (departureTime.isAfter(lastDepartureTime)) {
                    lastDepartureTime = departureTime;
                }
            }
        }
        return lastDepartureTime;
    }

    public void validateInputValues(int tableNumber, int capacity, int people, int disabilitiesPeople) {
        if (tableNumber < 1 || capacity < 0 || people < 0 || disabilitiesPeople < 0) {
            showInvalidInputAlert("Invalid input values. Please enter positive values for all fields.");
        }
    }

    public void validateTableNumber(int tableNumber) {
        if (tableNumber > 10) {
            showInvalidInputAlert("Table number " + tableNumber + " is not available for reservations. Please select a table with a number less than 10.");
        }
    }

    public LocalTime calculateArrivalTime(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return LocalTime.of(hours, minutes);
    }

    public boolean isReservationOverlapping(int tableNumber, LocalTime newArrivalTime, LocalTime newDepartureTime) {
        for (Reservation existingReservation : reservations) {
            if (existingReservation.getTableNumber() == tableNumber) {
                LocalTime existingArrival = LocalTime.parse(existingReservation.getArrivalTime());
                LocalTime existingDeparture = LocalTime.parse(existingReservation.getLeavingTime());

                if (newArrivalTime.isBefore(existingDeparture) && newDepartureTime.isAfter(existingArrival)) {
                    return true; // Conflicting reservation found
                }
            }
        }
        return false; // No conflicts found
    }

    public void showOverlapAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Reservation Overlap");
        alert.setHeaderText("The new reservation overlaps with an existing reservation.");
        alert.setContentText("Please select a different time or table.");
        alert.showAndWait();
    }

    public String calculateCategory(int people, int disabilitiesPeople) {
        return reservationSystem.calculateCategory(people, disabilitiesPeople);
    }

    public boolean validateTableCategory(int tableNumber, int capacity, String category) {
        String selectedTableCategory = getCategoryForTable(tableNumber);
        int calculatedCapacity = Integer.parseInt(peopleNumberField.getText()) + Integer.parseInt(disabilitiesPeopleNumberField.getText());

        if (!category.equals(selectedTableCategory) || capacity != calculatedCapacity) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Table Capacity or Category Mismatch");
            alert.setHeaderText("Table Capacity or Category Mismatch");
            alert.setContentText("The selected table's capacity or category does not match the calculated values for the group. Please select an appropriate table.");
            alert.showAndWait();

            return false; // Validation failed
        }

        return true; // Validation passed
    }


    public void showReservedAlert(int tableNumber, String time) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Table Already Reserved");
        alert.setHeaderText("Table " + tableNumber + " is already reserved during the selected time.");
        alert.setContentText("Please try a different table number or book at another time slot if all tables are booked.");
        alert.showAndWait();
    }

    public void addReservation(String name, String time, int tableNumber, int capacity) {
        Reservation reservation = new Reservation(name, time, tableNumber, capacity);
        reservations.add(reservation);
        reservationDisplays.add(new ReservationDisplay(
                reservation.getName(),
                reservation.getArrivalTime(),
                reservation.getTableNumber(),
                reservation.getCapacity()
        ));
        LocalTime arrivalTime = LocalTime.parse(reservation.getArrivalTime());
        LocalTime unlockTime = arrivalTime.plusHours(2);

        Duration timeUntilUnlock = calculateDuration(arrivalTime, unlockTime);

    /* Created a PauseTransition to delay unlocking the table. This strategy has been chosen to keep synchronous updating of reservations
    and their serialization into JSON, otherwise I could have used also a Timeline and keyframe! (async synchronization)
    */
        PauseTransition unlockTransition = new PauseTransition(timeUntilUnlock);
        unlockTransition.setOnFinished(evt -> {
            updateTableAvailability(reservation.getTableNumber(), String.valueOf(arrivalTime), String.valueOf(unlockTime), getCategoryForTable(tableNumber)); // Unlock the table
            serializeJsonFile();
        });
        unlockTransition.play();

        serializeJsonFile(); // Save changes to the JSON file

        // Store the PauseTransition in the tableTimers map
        tableTimers.put(reservation.getTable(), unlockTransition);

        clearInputFields();
    }

    public void clearInputFields() {
        nameField.clear();
        timeField.clear();
        tableNumberField.clear();
        capacityField.clear();
    }

    public void showInvalidNumberAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid table number or capacity");
        alert.setContentText("Please enter valid integer values for table number and capacity.");
        alert.showAndWait();
    }

    public void showInvalidInputAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid input values");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
