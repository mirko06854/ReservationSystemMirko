package merged;

import back.Reservation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMerged extends Application {
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ObservableList<ReservationDisplay> reservationDisplays = FXCollections.observableArrayList();
    private ObjectMapper objectMapper = new ObjectMapper();

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

                Reservation reservation = new Reservation(name, time, tableNumber, capacity);

                reservations.add(reservation);

                reservationDisplays.add(new ReservationDisplay(
                        reservation.getName(),
                        reservation.getTime(),
                        reservation.getTableNumber(),
                        reservation.getCapacity()
                ));

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
                alert.setHeaderText("Invalid table number or capacity");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
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

        TableView<ReservationDisplay> reservationTable = new TableView<>();
        reservationTable.getColumns().addAll(nameColumn, timeColumn, tableNumberColumn, capacityColumn);
        reservationTable.setItems(reservationDisplays);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(nameLabel, nameField, timeLabel, timeField, tableNumberLabel, tableNumberField, capacityLabel, capacityField, reserveButton, reservationTable);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
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
                    reservationDisplays.add(display);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            File file = new File("src/main/resources/tables.json");
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


