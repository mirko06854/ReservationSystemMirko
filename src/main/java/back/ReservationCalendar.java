package back;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * The ReservationCalendar class represents a JavaFX application for displaying a reservation calendar.
 * It extends the Application class, providing the entry point for the JavaFX application lifecycle.
 */
public class ReservationCalendar extends Application {
    // Instance variable to store the current date
    private LocalDate currentDate = LocalDate.now();

    /**
     * The main method to launch the ReservationCalendar JavaFX application.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Override of the start method from the Application class.
     * This method sets up the primary stage and constructs the user interface.
     * @param primaryStage The primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        // Set the title of the primary stage
        primaryStage.setTitle("Reservation Calendar");

        // Create the root BorderPane that will hold the main layout
        BorderPane root = new BorderPane();

        // Create a grid to represent the calendar
        GridPane calendarGrid = new GridPane();
        calendarGrid.getStyleClass().add("calendar-grid");
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setPadding(new Insets(40));
        calendarGrid.setAlignment(Pos.CENTER);

        // Update the calendar grid with buttons representing days
        updateCalendar(calendarGrid);

        // Create a VBox for holding other UI elements
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Label to display the current month and year
        Label monthYearLabel = new Label();
        monthYearLabel.getStyleClass().add("month-year-label");
        updateMonthYearLabel(monthYearLabel);

        // HBox for navigation buttons
        HBox navigationBox = new HBox(10);
        navigationBox.setAlignment(Pos.CENTER);

        // Button for navigating to the previous month
        Button backButton = new Button("Previous Month");
        backButton.getStyleClass().add("nav-button");
        backButton.setOnAction(event -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar(calendarGrid);
            updateMonthYearLabel(monthYearLabel);
        });

        // Button for navigating to the next month
        Button forwardButton = new Button("Next Month");
        forwardButton.getStyleClass().add("nav-button");
        forwardButton.setOnAction(event -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar(calendarGrid);
            updateMonthYearLabel(monthYearLabel);
        });

        // Add navigation buttons and label to the HBox
        navigationBox.getChildren().addAll(backButton, monthYearLabel, forwardButton);

        // Add the navigation box and calendar grid to the VBox
        vbox.getChildren().addAll(navigationBox, calendarGrid);

        // Set the center of the root BorderPane to the VBox
        root.setCenter(vbox);

        // Create the main scene with the specified dimensions
        Scene scene = new Scene(root, 800, 600);

        // Add an external stylesheet to the scene
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Set the scene to the primary stage and display it
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Update the calendar grid based on the current date.
     * @param calendarGrid The GridPane representing the calendar
     */
    private void updateCalendar(GridPane calendarGrid) {
        // Clear the existing children in the calendar grid
        calendarGrid.getChildren().clear();

        // Get the number of days in the current month and the first day of the month
        int daysInMonth = currentDate.lengthOfMonth();
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

        // Iterate over the days in the month and add buttons to the grid
        for (int i = 0; i < daysInMonth; i++) {
            Button dayButton = new Button(String.valueOf(i + 1));
            LocalDate currentDay = firstDayOfMonth.plusDays(i);

            // Attach an event handler to the button to handle day selection
            calendarGrid.add(dayButton, currentDay.getDayOfWeek().getValue() - 1, (i + firstDayOfMonth.getDayOfWeek().getValue() - 1) / 7);
        }
    }

    /**
     * Update the label to display the current month and year.
     * @param monthYearLabel The Label displaying the month and year
     */
    private void updateMonthYearLabel(Label monthYearLabel) {
        // Format the current date to display the month and year
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        monthYearLabel.setText(formattedDate);
    }
}
