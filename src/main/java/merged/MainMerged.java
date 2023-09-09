package merged;

import back.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

import static back.PlateManager.getAllPlates;

public class MainMerged extends Application implements MainMergedHelper {
    public ObservableList<Reservation> reservations = FXCollections.observableArrayList();
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

    Button selectDishesButton = new Button("Select Dishes for Clients");

    private Timer centralTimer = new Timer();


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

                if (!isValidTimeFormat(time)) {
                    showInvalidTimeFormatAlert();
                    return ;
                }

                // Get the most recent departure time for the selected table
                LocalTime lastDepartureTime = getLastDepartureTimeForTable(tableNumber);

                // Calculate new arrival and departure times
                LocalTime newArrivalTime = calculateArrivalTime(time);
                LocalTime newDepartureTime = newArrivalTime.plusHours(2);

                /* Note:  compare the new reservation's arrival time with the unlock time of the most recent reservation
                for the same table. If the new reservation's arrival time is after this unlock time, then there
                shouldn't be any overlap, and you can proceed to add the reservation. So I added the logic and to fix unexpected warnings !
                 MOVED!!! */

                // Calculate the unlock time based on the last departure time
                LocalTime unlockTime = lastDepartureTime.plusMinutes(1); // Add a small buffer

                // Check if new reservation overlaps with existing reservations
                if (isReservationOverlapping(tableNumber, newArrivalTime, newDepartureTime)) {
                    showOverlapAlert();
                    return;
                }
                // Check table number validity
                validateTableNumber(tableNumber);

                // Validate table booking time
                ReservationSystem.validateTableBookingTime(time);

                // validates the category and check if it matches the table
                validateTableCategory(tableNumber, capacity, category);

                // Validate category and capacity
                if (!validateTableCategory(tableNumber, capacity, category)) {
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

        selectDishesButton.setOnAction(event -> {
            // Get the selected reservation
            ReservationDisplay selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null) {
                Reservation reservation = findReservation(selectedReservation);
                if (reservation != null) {
                    // Open the popup to select dishes for the selected reservation
                    openDishesPopup(reservation);
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

        // Add a new button column to the reservation table
        TableColumn<ReservationDisplay, Void> viewFoodColumn = new TableColumn<>("View Food");
        viewFoodColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewFoodButton = new Button("View Food");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewFoodButton);

                    // Get the selected reservation for this row
                    ReservationDisplay reservationDisplay = getTableView().getItems().get(getIndex());
                    Reservation reservation = findReservation(reservationDisplay);
                    // Handle button click event
                    viewFoodButton.setOnAction(event -> {
                        if (reservation != null) {
                            Map<String, Integer> orderedPlatesMap = reservation.getPlatesMap();
                            // Display ordered plates
                            showOrderedFoodDialog(orderedPlatesMap,reservation);
                        }
                    });
                }
            }
        });


        reservationTable = new TableView<>();
        reservationTable.setId("reservationTable");
        reservationTable.getColumns().addAll(nameColumn, timeColumn, tableNumberColumn, capacityColumn,viewFoodColumn);
        reservationTable.setItems(reservationDisplays);

        // Add a listener to enable/disable the "Select Dishes for Clients" button based on row selection
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectDishesButton.setDisable(false); // Enable the button when a row is selected
            } else {
                selectDishesButton.setDisable(true); // Disable the button when no row is selected
            }
        });

        layout = new VBox(10);
        layout.getChildren().addAll(nameLabel, nameField, timeLabel, timeField, tableNumberLabel, tableNumberField, capacityLabel, capacityField, peopleNumberLabel, peopleNumberField,
                disabilitiesPeopleNumberLabel, disabilitiesPeopleNumberField, reserveButton, deleteButton, cleanButton, reservationTable, selectDishesButton);
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
        selectDishesButton.setId("selectDish");

        /* Set up a scheduled task using centralTimer to periodically run processUnlockEvents()
         This task will execute every second (1000 milliseconds) to check for unlock events
         */
        centralTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                processUnlockEvents(); // Check and handle unlock events
            }
        }, 0, 1000); // Check every second for unlock events
    }

    public void showOrderedFoodDialog(Map<String, Integer> orderedPlatesMap, Reservation reservation) {

        ReservationDisplay selectedReservation = reservationTable.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ordered Food");
        alert.setHeaderText("Ordered Plates and Quantities");

        // Create a VBox to display the ordered plates, quantities, and pay buttons
        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER_LEFT);

        // Iterate over the ordered plates map and add them to the VBox
        for (Map.Entry<String, Integer> entry : orderedPlatesMap.entrySet()) {
            String plateName = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > 0) { // Only show plates with a positive quantity
                HBox hBox = new HBox(10);
                hBox.setAlignment(Pos.CENTER_LEFT);

                // Create a Label to display the plate name and quantity
                Label label = new Label(plateName + ": " + quantity);

                // Create a Pay button
                Button payButton = new Button("Pay");
                payButton.setOnAction(event -> {
                    // Decrement the quantity of the item in the reservation's platesMap
                    reservation.decrementPlateQuantity(plateName, 1); // Decrement by 1

                    // Update the label text to reflect the decremented quantity
                    int updatedQuantity = reservation.getPlatesMap().getOrDefault(plateName, 0);
                    label.setText(plateName + ": " + updatedQuantity);

                    // If the quantity becomes zero, remove the item from the GUI
                    if (updatedQuantity <= 0) {
                        vBox.getChildren().remove(hBox);
                        // Check if all plates have been paid
                        if (areAllPlatesPaid(reservation)) {
                            if (selectedReservation != null) {
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
                    }
                });

                // Add the Label and Pay button to the HBox
                hBox.getChildren().addAll(label, payButton);

                // Add the HBox to the VBox
                vBox.getChildren().add(hBox);
            }
        }

        alert.getDialogPane().setContent(vBox);

        // Show the dialog
        alert.showAndWait();
    }


    // Helper method to check if all plates have been paid
    private boolean areAllPlatesPaid(Reservation reservation) {
        Map<String, Integer> platesMap = reservation.getPlatesMap();
        for (int quantity : platesMap.values()) {
            if (quantity > 0) {
                return false; // At least one plate is not paid
            }
        }
        return true; // All plates are paid
    }




    private void openDishesPopup(Reservation reservation) {
        // Create a dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Select Dishes");
        dialog.setHeaderText("Select dishes and quantity for the reservation:");

        // Create a dialog pane
        DialogPane dialogPane = new DialogPane();
        dialog.setDialogPane(dialogPane);

        // Create a GridPane to display plates and quantity selectors
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Create checkboxes and quantity selectors for each plate
        List<CheckBox> checkboxes = new ArrayList<>();
        List<Spinner<Integer>> quantitySpinners = new ArrayList<>();

        // Get all available plates from PlateManager
        List<Plate> allPlates = PlateManager.getAllPlates();

        for (int i = 0; i < allPlates.size(); i++) {
            Plate plate = allPlates.get(i);
            CheckBox checkbox = new CheckBox(plate.getName());
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1); // Customize the spinner range as needed
            checkboxes.add(checkbox);
            quantitySpinners.add(quantitySpinner);

            // Add checkboxes and quantity selectors to the grid
            grid.add(checkbox, 0, i);
            grid.add(quantitySpinner, 1, i);
        }

        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons to the dialog
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        // Declare a Map to store selected plates and their quantities
        Map<String, Integer> selectedPlatesMap = new HashMap<>();

        // When OK is clicked, collect the selected plates and quantities
        dialog.setResultConverter(buttonType -> {
            if (buttonType == buttonTypeOk) {
                for (int i = 0; i < checkboxes.size(); i++) {
                    if (checkboxes.get(i).isSelected()) {
                        Plate plate = allPlates.get(i);
                        int quantity = quantitySpinners.get(i).getValue();
                        selectedPlatesMap.put(plate.getName(), quantity);
                    }
                }
                // Update the reservation with the selected plates map
                reservation.setPlatesMap(selectedPlatesMap);
            }
            return null; // Return null for other cases (e.g., Cancel)
        });

        // Show the dialog
        dialog.showAndWait();
    }
    private void processUnlockEvents() {
        LocalTime currentTime = LocalTime.now(); //  used to retrieve the current local time based on the system clock.

        // Iterate through all reservations to check for unlock events
        for (Reservation reservation : reservations) {
            // Check if the reservation's unlock time is before the current time and if it's still locked
            if (reservation.getUnlockTime().isBefore(currentTime) && reservation.isLocked()) {
                Table table = reservation.getTable();
                // Set the table as available again and update its availability in JSON data
                updateTableAvailability(table.getTableNumber(), reservation.getArrivalTime(), reservation.getLeavingTime(), getCategoryForTable(table.getTableNumber())); // Unlock the table
                reservation.setLocked(false); // Mark reservation as unlocked
                serializeJsonFile(); // Save changes to JSON file
            }
        }
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

    public boolean isValidTimeFormat(String time) {
        return time.matches("^\\d{2}:\\d{2}$");
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

    private void showInvalidTimeFormatAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Time Format");
        alert.setHeaderText("Invalid time format");
        alert.setContentText("Please enter the time in the format HH:mm (e.g., 09:00).");
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