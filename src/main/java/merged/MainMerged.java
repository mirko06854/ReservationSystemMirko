package merged;

import back.Reservation;
import back.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.util.Duration;

public class MainMerged extends Application {
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ObservableList<ReservationDisplay> reservationDisplays = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<Table, Timeline> tableTimers = new HashMap<>(); // mapping each table with its timeline

    private TableView<ReservationDisplay> reservationTable; // Declare the reservationTable variable here


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {


        primaryStage.setTitle("Reservation System");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();

        Label tableNumberLabel = new Label("Table Number:");
        TextField tableNumberField = new TextField();

        Label capacityLabel = new Label("Capacity:");
        TextField capacityField = new TextField();

        Button reserveButton = new Button("Reserve");
        Button cleanButton = new Button("Clean JSON");
        Button deleteButton = new Button("Delete Reservation");

        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        reserveButton.setOnAction(e -> {
            String name = nameField.getText();
            String time = timeField.getText();
            int tableNumber;
            int capacity;

            try {
                tableNumber = Integer.parseInt(tableNumberField.getText());
                capacity = Integer.parseInt(capacityField.getText());

                if (tableNumber < 0 || capacity < 0) {
                    throw new IllegalArgumentException("Table number and capacity cannot be negative.");
                }

                // Validate the table booking time
                back.ReservationSystem.validateTableBookingTime(time);

                Reservation reservation = new Reservation(name, time, tableNumber, capacity);

                reservations.add(reservation);

                reservationDisplays.add(new ReservationDisplay(
                        reservation.getName(),
                        reservation.getTime(),
                        reservation.getTableNumber(),
                        reservation.getCapacity()
                ));

                updateTableAvailability(reservation.getTableNumber(), false); // Set the table as not available
                serializeJsonFile(); // Save changes to the JSON file

                nameField.clear();
                timeField.clear();
                tableNumberField.clear();
                capacityField.clear();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid table number or capacity");
                alert.setContentText("Please enter valid integer values for table number and capacity.");
                alert.showAndWait();
            } catch (IllegalArgumentException ex) {
                if (ex.getMessage().equals("Invalid table booking time")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Invalid table booking time");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Invalid table number or capacity");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        cleanButton.setOnAction(e ->  cleanJson());

        deleteButton.setOnAction(e -> {
            ReservationDisplay selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
            if (selectedReservation != null) {
                Reservation reservation = findReservation(selectedReservation);
                if (reservation != null) {
                    reservations.remove(reservation);
                    reservationDisplays.remove(selectedReservation);
                    updateTableAvailability(reservation.getTableNumber(), true); // Set the table as available again
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
        layout.getChildren().addAll(nameLabel, nameField, timeLabel, timeField, tableNumberLabel, tableNumberField, capacityLabel, capacityField, reserveButton, deleteButton, cleanButton, reservationTable);
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

    private void loadReservedTables() {
        try {
            File file = new File("src/main/resources/tables.json");
            if (file.exists()) {
                Reservation[] loadedTables = objectMapper.readValue(file, Reservation[].class);

                for (Reservation reservation : loadedTables) {
                    ReservationDisplay display = new ReservationDisplay(
                            reservation.getName(),
                            reservation.getTime(),
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

    // Method to find the Reservation object corresponding to the selected ReservationDisplay
    private Reservation findReservation(ReservationDisplay selectedReservation) {
        for (Reservation reservation : reservations) {
            if (reservation.getName().equals(selectedReservation.getName().get()) &&
                    reservation.getTime().equals(selectedReservation.getTime().get()) &&
                    reservation.getTableNumber() == selectedReservation.getTableNumber().get() &&
                    reservation.getCapacity() == selectedReservation.getCapacity().get()) {
                return reservation;
            }
        }
        return null;
    }

    private void updateTableAvailability(int tableNumber, boolean isAvailable) {
        for (Reservation reservation : reservations) {
            if (reservation.getTableNumber() == tableNumber) {
                reservation.setAvailable(isAvailable);
                break;
            }
        }
    }

    private void serializeJsonFile() {
        File file = new File("src/main/resources/tables.json");
        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cleanJson() {
        reservations.clear();
        reservationDisplays.clear();
        serializeJsonFile();
    }

    @Override
    public void stop() {
        // Save changes to the JSON file when the application is closed
        serializeJsonFile();
    }
}
