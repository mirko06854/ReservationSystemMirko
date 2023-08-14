package front;

import back.Reservation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This is just the front end, not retrieving any JSON
 */

public class ReservationApp extends Application {

    private TableView<Reservation> reservationTable;
    private ObservableList<Reservation> reservations;

    public ObservableList<Reservation> getReservations() {
        return reservations;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Reservation App");

        // Create the table columns
        TableColumn<Reservation, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data -> data.getValue().getNameProperty());

        TableColumn<Reservation, String> timeColumn = new TableColumn<>("arrivalTime");
        timeColumn.setCellValueFactory(data -> data.getValue().getArrivalTimeProperty());

        TableColumn<Reservation, Integer> tableNumberColumn = new TableColumn<>("Table Number");
        tableNumberColumn.setCellValueFactory(data -> data.getValue().getTableNumberProperty().asObject());
        tableNumberColumn.setCellFactory(column -> new TableCell<Reservation, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item));
                }
            }
        });

        TableColumn<Reservation, Integer> capacityColumn = new TableColumn<>("Capacity");
        capacityColumn.setCellValueFactory(data -> data.getValue().getCapacityProperty().asObject());
        capacityColumn.setCellFactory(column -> new TableCell<Reservation, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item));
                }
            }
        });

        // Create the table and set the columns
        reservationTable = new TableView<>();
        reservationTable.getColumns().addAll(nameColumn, timeColumn, tableNumberColumn, capacityColumn);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();
        Label tableNumberLabel = new Label("Table Number: ");
        TextField tableNumberField = new TextField();
        Label capacityLabel = new Label("Capacity: ");
        TextField capacityField = new TextField();
        Button reserveButton = new Button("Reserve");

        reserveButton.setOnAction(e -> {
            String name = nameField.getText();
            String arrivalTime = timeField.getText(); // Changed from time to arrivalTime
            int tableNumber;
            int capacity;

            try {
                tableNumber = Integer.parseInt(tableNumberField.getText());
                capacity = Integer.parseInt(capacityField.getText());

                if (tableNumber < 0 || capacity < 0) {
                    throw new IllegalArgumentException("Table number and capacity cannot be negative.");
                }

                // Validate the table booking time
                validateTableBookingTime(arrivalTime); // Changed from time to arrivalTime

                Reservation reservation = new Reservation(name, arrivalTime, tableNumber, capacity);
                reservations.add(reservation);

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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid table booking time");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });


        VBox layout = new VBox(10);
        layout.getChildren().addAll(nameLabel, nameField, timeLabel, timeField, tableNumberLabel, tableNumberField, capacityLabel, capacityField, reserveButton, reservationTable);
        layout.setSpacing(10);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        reservations = FXCollections.observableArrayList();
        reservationTable.setItems(reservations);
    }

    public void addReservation(String name, String time, int tableNumber, int capacity) {

        /* If reservations is null, it means that the list has not been initialized yet. In that case, the code initializes the reservations list by creating a new instance of ObservableList using FXCollections.observableArrayList().
        The FXCollections.observableArrayList() method creates an empty ObservableList object that can be used to store elements and provides features to observe and track changes to the list.
        */

        if (reservations == null) {
            reservations = FXCollections.observableArrayList();
        }

        Reservation reservation = new Reservation(name, time, tableNumber, capacity);
        reservations.add(reservation);
    }

    public void setReservations(ObservableList<Object> observableArrayList) {
        this.reservations = reservations;
    }

    public static TextField getTableNumberField() {
        TextField tableNumberField = new TextField();
        return tableNumberField;
    }

    public static TextField getCapacityField() {
        TextField capacityField = new TextField();
        return capacityField;
    }

    //the same method tested in the tests defined for the back end, to merge later in the merged package
    private void validateTableBookingTime(String time) {
        if (!time.matches("^([01]?[0-9]|2[0-4]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Invalid table booking time: " + time);
        }
    }
}




