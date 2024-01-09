package merged;

import back.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
import javafx.stage.Modality;


public class MainMerged extends Application implements MainMergedHelper{
    public ArrayList<Reservation> reservations = new ArrayList<>();
    private final ObservableList<ReservationDisplay> reservationDisplays = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private TableView<ReservationDisplay> reservationTable;

    Label nameLabel = new Label("Name:");
    TextField nameField = new TextField();

    Label timeLabel = new Label("Time:");
    TextField timeField = new TextField();

    Label tableNumberLabel = new Label("Table Number:");
    TextField tableNumberField = new TextField();

    Label peopleNumberLabel = new Label("people:");
    TextField peopleNumberField = new TextField();

    Label disabilitiesPeopleNumberLabel = new Label("people with disabilities:");
    TextField disabilitiesPeopleNumberField = new TextField();

    Button reserveButton = new Button("Reserve");
    Button cleanButton = new Button("Clean JSON");
    Button deleteButton = new Button("Delete Reservation");

    Button selectDishesButton = new Button("Select Dishes for Clients");

    Button backToCalendarPopUpButton = new Button("Select day");

    private ReservationCalendar reservationCalendar;

    private Stage primaryStage;

    // Create the new Stage for the selection of date
    private Stage calendarPopup;

    public MainMerged() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Reservation System");

        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // turns off the button for reserving when the app is runned the first time
        reserveButton.setDisable(true);


        reserveButton.setOnAction(e -> {
            try {

                // Get input values
                String name = nameField.getText();
                String time = timeField.getText();
                int tableNumber = Integer.parseInt(tableNumberField.getText());
                int people = Integer.parseInt(peopleNumberField.getText());
                int disabilitiesPeople = Integer.parseInt(disabilitiesPeopleNumberField.getText());
                int totalPeople = people + disabilitiesPeople;

                //calculates the category:
                String category = calculateCategory(people, disabilitiesPeople);

                // Validate input values
                validateInputValues(tableNumber, people, disabilitiesPeople);

                // Validate input values
                if (totalPeople > 5) {
                    showInvalidInputAlert("Number of people and disabilities exceeds the table capacity (5).");
                    return;
                }

                if (tableNumber < 1 || people < 0 || disabilitiesPeople < 0) {
                    showInvalidInputAlert("enter other positive values ");
                    return; // Exit the method without proceeding further
                }

                if (tableNumber > 10) {
                    showInvalidInputAlert("enter table number < 11");
                    return; // Exit the method without proceeding further
                }

                if (category.equals("Special Needs") && (totalPeople > 3)) {
                    showInvalidInputAlert("For tables with category 'Special Needs', the capacity should be the sum of people , and that must be max 3");
                    if (disabilitiesPeople == 5) {
                        showRecommendation(); // Suggest to add such group to two tables with category = special needs.
                    }
                    return; // Exit the method without proceeding further
                }

                // Check if the sum of people is greater than 3 and at least 3 are people with disabilities
                if (totalPeople > 3 && disabilitiesPeople < people) {
                    category = "Normal"; // Set category to "Normal" in this special case
                }

                if (!isValidTimeFormat(time)) {
                    showInvalidTimeFormatAlert();
                    return;
                }

                // Calculate new arrival and departure times
                LocalTime newArrivalTime = calculateArrivalTime(time);
                LocalTime newDepartureTime = newArrivalTime.plusHours(2);


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
                validateTableCategory(tableNumber, totalPeople, category);

                // Validate category and capacity
                if (!validateTableCategory(tableNumber, totalPeople, category)) {
                    return; // Exit the method without adding the reservation
                }

                // Create and store the reservation
                addReservation(name, time, tableNumber, totalPeople);

                // Clear input fields
                clearInputFields();

            } catch (NumberFormatException ex) {
                showInvalidNumberAlert();
            } catch (IllegalArgumentException ex) {
                showInvalidInputAlert(ex.getMessage());
            }
        });

        cleanButton.setOnAction(e -> {
            cleanJsonAndReservations();
        });


        deleteButton.setOnAction(e -> {
            deleteEachReservation();
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

        // Called when the button "Select Day" is pressed
        backToCalendarPopUpButton.setOnAction(event -> {
            initializeCalendarPopup();
            // turns on the button to reserve tables
            reserveButton.setDisable(false);
        });

        loadReservedTables();
        TableColumn<ReservationDisplay, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ReservationDisplay, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<ReservationDisplay, Integer> tableNumberColumn = new TableColumn<>("Table Number");
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));

        // Add a new button column to the reservation table
        TableColumn<ReservationDisplay, Void> viewFoodColumn = getReservationDisplayVoidTableColumn();

        reservationTable = new TableView<>();
        reservationTable.setId("reservationTable");
        reservationTable.getColumns().addAll(nameColumn, timeColumn, tableNumberColumn, viewFoodColumn);
        reservationTable.setItems(reservationDisplays);

        // Add a listener to enable/disable the "Select Dishes for Clients" button based on row selection
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // Disable the button when no row is selected
            selectDishesButton.setDisable(newSelection == null); // Enable the button when a row is selected
        });

        layout = new VBox(10);
        layout.getChildren().addAll(nameLabel, nameField, timeLabel, timeField, tableNumberLabel, tableNumberField, peopleNumberLabel, peopleNumberField,
                disabilitiesPeopleNumberLabel, disabilitiesPeopleNumberField, reserveButton, deleteButton, cleanButton, reservationTable, selectDishesButton,backToCalendarPopUpButton);
        layout.setPadding(new Insets(10));
        scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();


        // Set the FX IDs for the UI elements
        nameField.setId("nameField");
        timeField.setId("timeField");
        tableNumberField.setId("tableNumberField");
        reserveButton.setId("reserveButton");
        cleanButton.setId("cleanButton");
        deleteButton.setId("deleteButton");
        reservationTable.setId("reservationTable");
        selectDishesButton.setId("selectDish");
        backToCalendarPopUpButton.setId("backButton");

    }

    private void initializeCalendarPopup() {
        calendarPopup = new Stage();
        calendarPopup.initModality(Modality.APPLICATION_MODAL);
        calendarPopup.setTitle("Select Date");

        // Create an instance of ReservationCalendar und show it up in the new Stage
        ReservationCalendar reservationCalendar = new ReservationCalendar(this);
        reservationCalendar.start(calendarPopup);
    }

    private TableColumn<ReservationDisplay, Void> getReservationDisplayVoidTableColumn() {
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
                            // Display ordered plates
                            showOrderedFoodDialog(reservation, reservationDisplay);
                        }
                    });
                }
            }
        });
        return viewFoodColumn;
    }

    public void showOrderedFoodDialog(Reservation reservation, ReservationDisplay selectedReservation) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ordered Food");
        alert.setHeaderText("Ordered Plates and Quantities");

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.CENTER_LEFT);

        if (reservation.getPlatesMap() != null) {
            for (Map.Entry<String, Integer> entry : reservation.getPlatesMap().entrySet()) {
                String plateName = entry.getKey();
                int quantity = entry.getValue();

                if (quantity > 0) {
                    HBox hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    Label label = new Label(plateName + ": " + quantity);

                    Button payButton = new Button("Pay");
                    payButton.setOnAction(event -> handlePlates(plateName, reservation, hBox, label, vBox));

                    hBox.getChildren().addAll(label, payButton);
                    vBox.getChildren().add(hBox);
                }
            }
        }

        alert.getDialogPane().setContent(vBox);
        alert.showAndWait();
    }
    public boolean areAllPlatesPaid(Reservation reservation) {
        Map<String, Integer> platesMap = reservation.getPlatesMap();
        for (int quantity : platesMap.values()) {
            if (quantity > 0) {
                return false; // At least one plate is not paid
            }
        }
        return true; // All plates are paid
    }


    public void openDishesPopup(Reservation reservation) {
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

        // Initialize selectedPlatesMap with the existing plates for the reservation
        Map<String, Integer> selectedPlatesMap = new HashMap<>(reservation.getPlatesMap());

        for (int i = 0; i < allPlates.size(); i++) {
            Plate plate = allPlates.get(i);
            CheckBox checkbox = new CheckBox(plate.name());
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1); // Customize the spinner range as needed
            checkboxes.add(checkbox);
            quantitySpinners.add(quantitySpinner);

            // Set the checkbox as selected if the plate is in the selectedPlatesMap
            if (selectedPlatesMap.containsKey(plate.name())) {
                checkbox.setSelected(true);
                quantitySpinner.getValueFactory().setValue(selectedPlatesMap.get(plate.name()));
            }

            // Add checkboxes and quantity selectors to the grid
            grid.add(checkbox, 0, i);
            grid.add(quantitySpinner, 1, i);
        }

        dialog.getDialogPane().setContent(grid);

        // Add OK and Cancel buttons to the dialog
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        // When OK is clicked, collect the selected plates and quantities
        dialog.setResultConverter(buttonType -> {
            if (buttonType == buttonTypeOk) {
                for (int i = 0; i < checkboxes.size(); i++) {
                    if (checkboxes.get(i).isSelected()) {
                        Plate plate = allPlates.get(i);
                        int quantity = quantitySpinners.get(i).getValue();

                        // Append the new plates and quantities to the existing selectedPlatesMap
                        String plateName = plate.name();
                        int existingQuantity = selectedPlatesMap.getOrDefault(plateName, 0);
                        selectedPlatesMap.put(plateName, existingQuantity + quantity);
                    }
                }
                // Update the reservation with the updated selectedPlatesMap
                reservation.setPlatesMap(selectedPlatesMap);
            }
            return null; // Return null for other cases (e.g., Cancel)
        });

        // Show the dialog
        dialog.showAndWait();
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

    public void updateTableAvailability(int tableNumber, String newReservationArrivalTime, String newReservationLeavingTime, String calculatedCategory) {
        for (Reservation reservation : reservations) {
            if (reservation.getTableNumber() == tableNumber) {
                LocalTime existingArrivalTime = LocalTime.parse(reservation.getArrivalTime());
                LocalTime existingLeavingTime = existingArrivalTime.plusHours(2); // Calculate leaving time as 2 hours after arrival

                LocalTime newResArrivalTime = LocalTime.parse(newReservationArrivalTime);
                LocalTime newResLeavingTime = newResArrivalTime.plusHours(2); // Calculate leaving time as 2 hours after arrival

                // Check if new reservation overlaps with an existing reservation
                if ((newResArrivalTime.isBefore(existingLeavingTime) && newResLeavingTime.isAfter(existingArrivalTime)) ||
                        (newResLeavingTime.isAfter(existingArrivalTime) && newResArrivalTime.isBefore(existingLeavingTime))) {
                    return; // New reservation overlaps with an existing reservation
                }
            }
        }
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

    public void deleteEachReservation() {
        ReservationDisplay selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Reservation reservation = findReservation(selectedReservation);
            if (reservation != null) {
                reservations.remove(reservation);
                reservationDisplays.remove(selectedReservation);
                updateTableAvailability(reservation.getTableNumber(), reservation.getArrivalTime(), reservation.getLeavingTime(), getCategoryForTable(reservation.getTableNumber())); // Set the table as available again
                serializeJsonFile(); // Save changes to the JSON file
            }
        }
    }

    public void cleanJsonAndReservations() {
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

    public void validateInputValues(int tableNumber, int people, int disabilitiesPeople) {
        if (tableNumber < 1 || people < 0 || disabilitiesPeople < 0) {
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

    public void showRecommendation() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("spit");
        alert.setHeaderText("adding groups together");
        alert.setContentText("Please split such people in 2 tables designed for people with Special Needs.");
        alert.showAndWait();
    }

    public String calculateCategory(int people, int disabilitiesPeople) {
        return ReservationSystem.calculateCategory(people, disabilitiesPeople);
    }

    public boolean validateTableCategory(int tableNumber, int capacity, String category) {
        String selectedTableCategory = getCategoryForTable(tableNumber);
        int calculatedCapacity = Integer.parseInt(peopleNumberField.getText()) + Integer.parseInt(disabilitiesPeopleNumberField.getText());

        if (!category.equals(selectedTableCategory) || capacity < calculatedCapacity) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Table Capacity or Category Mismatch");
            alert.setHeaderText("Table Capacity or Category Mismatch");
            alert.setContentText("The selected table's capacity or category does not match the calculated values for the group. Please select an appropriate table.");
            alert.showAndWait();

            return false; // Validation failed
        }

        return true; // Validation passed
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
        serializeJsonFile();
        clearInputFields();
    }

    public void clearInputFields() {
        nameField.clear();
        timeField.clear();
        tableNumberField.clear();
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

     public void updateReservationsDisplay(List<Reservation> reservations) {
        // Clear the existing items in the reservationDisplays list
        cleanJsonAndReservations();

        // Add the new reservations to the reservationDisplays list
        if (reservations != null) {
            for (Reservation reservation : reservations) {
                reservationDisplays.add(new ReservationDisplay(
                        reservation.getName(),
                        reservation.getArrivalTime(),
                        reservation.getTableNumber(),
                        reservation.getCapacity()
                ));
            }
        }
    }

    private void handlePlates(String plateName, Reservation reservation, HBox hBox, Label label, VBox vBox) {
        reservation.decrementPlateQuantity(plateName); // Decrement by 1

        int updatedQuantity = reservation.getPlatesMap().getOrDefault(plateName, 0);
        label.setText(plateName + ": " + updatedQuantity);

        if (updatedQuantity <= 0) {
            vBox.getChildren().remove(hBox);
            if (areAllPlatesPaid(reservation)) {
                deleteEachReservation();
            }
        }
    }
}