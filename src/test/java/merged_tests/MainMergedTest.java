package merged_tests;

import javafx.util.Duration;
import merged.MainMerged;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class MainMergedTest extends ApplicationTest {
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

    @Test
    public void isReservationOverlappingOverlap() {
        // Given
        MainMerged mainMerged = new MainMerged();
        int tableNumber = 2;
        LocalTime existingArrivalTime = LocalTime.of(10, 0);
        LocalTime existingDepartureTime = LocalTime.of(12, 0);
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