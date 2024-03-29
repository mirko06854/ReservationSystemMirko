package front_tests;

import front.ReservationApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationAppTest {

    private ReservationApp reservationApp;

    @BeforeEach
    void setUp() {
        reservationApp = new ReservationApp();
    }

    @Test
    void testAddReservation_ValidInput() {
        // Simulate input of valid values
        String name = "John Doe";
        String arrivalTime = "12:30";
        String validTableNumber = "2";
        String validCapacity = "6";

        // Call the addReservation method
        assertDoesNotThrow(() -> reservationApp.addReservation(name, arrivalTime, Integer.parseInt(validTableNumber), Integer.parseInt(validCapacity)));

    }

    @Test
    void testAddReservation_InvalidInput() {
        // Simulate input of invalid values
        String name = "John Doe";
        String arrivalTime = "12:30";
        String invalidTableNumber = "A1";
        String invalidCapacity = "5 guests";

        // Call the addReservation method
        assertThrows(NumberFormatException.class, () -> reservationApp.addReservation(name, arrivalTime, Integer.parseInt(invalidTableNumber), Integer.parseInt(invalidCapacity)));

    }
}
