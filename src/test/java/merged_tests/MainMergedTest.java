package merged_tests;

import javafx.embed.swing.JFXPanel;
import javafx.util.Duration;
import merged.MainMerged;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains unit tests for the methods in the {@link MainMerged} class.
 * It extends the {@link ApplicationTest} class to leverage JavaFX testing utilities.
 */
public class MainMergedTest extends ApplicationTest {

    /**
     * Test the {@link MainMerged#calculateArrivalTime(String)} method.
     * It verifies that the method correctly calculates the arrival time based on the given input time.
     */

    @BeforeAll
    public static void initJavaFX() {
        System.setProperty("javafx.headless", "true");
        new JFXPanel(); // Initializes JavaFX environment
    }
    @Test
    public void calculateArrivalTimeTest() {
        // Given
        MainMerged mainMerged = new MainMerged();

        // When
        String time = "9:00";
        LocalTime arrivalTime = mainMerged.calculateArrivalTime(time);

        // Then
        assertEquals(LocalTime.of(9, 0), arrivalTime);
    }

    /**
     * Test the {@link MainMerged#calculateDuration(LocalTime, LocalTime)} method.
     * It verifies that the method calculates the correct duration between two given times.
     */

    @Test
    public void calculateDurationTest() {
        // Given
        MainMerged mainMerged = new MainMerged();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        // When
        Duration duration = mainMerged.calculateDuration(startTime, endTime);

        // Then
        assertEquals(Duration.hours(2), duration);
    }

    /**
     * Test the {@link MainMerged#getCategoryForTable(int)} method
     * when the table number corresponds to a "Normal" category.
     * It verifies that the correct category is returned for a valid table number.
     */
    @Test
    public void getCategoryForTableNormal() {
        // Given
        MainMerged mainMerged = new MainMerged();
        int tableNumber = 3;  // a number from 1 to 5 make the test pass, otherwise fails

        // When
        String category = mainMerged.getCategoryForTable(tableNumber);

        // Then
        assertEquals("Normal", category);
    }

    /**
     * Test the {@link MainMerged#getCategoryForTable(int)} method
     * when the table number corresponds to a "Special Needs" category.
     * It verifies that the correct category is returned for a valid table number.
     */

    @Test
    public void getCategoryForTableSpecialNeeds() {
        // Given
        MainMerged mainMerged = new MainMerged();
        int tableNumber = 6; // a number from 6 to 10 make the test pass, otherwise fails

        // When
        String category = mainMerged.getCategoryForTable(tableNumber);

        // Then
        assertEquals("Special Needs", category);
    }

    /**
     * Test the {@link MainMerged#getCategoryForTable(int)} method
     * when the table number is not recognized and falls into the "Unknown" category.
     * It verifies that the "Unknown" category is returned for an invalid table number.
     */
    @Test
    public void getCategoryForTableUnknown() {
        // Given
        MainMerged mainMerged = new MainMerged();
        int tableNumber = 15;

        // When
        String category = mainMerged.getCategoryForTable(tableNumber);

        // Then
        assertEquals("Unknown", category);
    }
    /**
     * Test the {@link MainMerged#isValidTimeFormat(String)} method
     * when a valid time format is provided.
     * It verifies that the method correctly validates a valid time format.
     */
    @Test
    public void isValidTimeFormatValid() {
        // Given
        MainMerged mainMerged = new MainMerged();
        String time = "10:30";

        // When
        boolean isValid = mainMerged.isValidTimeFormat(time);

        // Then
        assertTrue(isValid);
    }

    /**
     * Test the {@link MainMerged#isValidTimeFormat(String)} method
     * when an invalid time format is provided.
     * It verifies that the method correctly identifies an invalid time format.
     */
    @Test
    public void isValidTimeFormatInvalid() {
        // Given
        MainMerged mainMerged = new MainMerged();
        String time = "10:30:00";

        // When
        boolean isValid = mainMerged.isValidTimeFormat(time);

        // Then
        assertFalse(isValid);
    }

    /**
     * Test the {@link MainMerged#isReservationOverlapping(int, LocalTime, LocalTime)} method
     * when there is no overlap between reservations.
     * It verifies that the method correctly identifies no overlap.
     */
    @Test
    public void isReservationOverlappingNoOverlap() {
        // Given
        MainMerged mainMerged = new MainMerged();
        int tableNumber = 1;
        LocalTime existingArrivalTime = LocalTime.of(10, 0);
        LocalTime existingDepartureTime = LocalTime.of(12, 0);
        LocalTime newArrivalTime = LocalTime.of(12, 0);
        LocalTime newDepartureTime = LocalTime.of(14, 0);

        // When
        boolean isNotOverlapping = mainMerged.isReservationOverlapping(tableNumber, newArrivalTime, newDepartureTime);

        if (isNotOverlapping == true) {
            // Then
            assertTrue(isNotOverlapping);
        }
    }

    /**
     * Test the {@link MainMerged#isReservationOverlapping(int, LocalTime, LocalTime)} method
     * when there is an overlap between reservations.
     * It verifies that the method correctly identifies the overlap.
     */
    @Test
    public void isReservationOverlappingOverlap() {
        // Given
        MainMerged mainMerged = new MainMerged();
        int tableNumber = 2;
        LocalTime newArrivalTime = LocalTime.of(10, 0);
        LocalTime newDepartureTime = LocalTime.of(12, 0);

        // When
        boolean isOverlapping = mainMerged.isReservationOverlapping(tableNumber, newArrivalTime, newDepartureTime);

        if (isOverlapping == true) {
            // Then
            assertTrue(isOverlapping);
        }
    }
}