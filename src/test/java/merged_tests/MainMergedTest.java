// TestFX:

package merged_tests;

        import javafx.application.Platform;
        import javafx.scene.input.KeyCode;
        import javafx.scene.input.MouseButton;
        import javafx.stage.Stage;
        import merged.MainMerged;
        import merged.ReservationDisplay;
        import org.junit.jupiter.api.AfterEach;
        import org.junit.jupiter.api.BeforeAll;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.testfx.api.FxToolkit;
        import org.testfx.framework.junit5.ApplicationTest;
        import org.testfx.util.WaitForAsyncUtils;

        import java.util.concurrent.TimeUnit;

        import static org.junit.jupiter.api.Assertions.assertFalse;
        import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * Test class for testing the functionality of the MainMerged application using TestFX.
 * This class extends ApplicationTest, which provides support for testing JavaFX applications.
 */
public class MainMergedTest extends ApplicationTest {

    /**
     * Set up before all test cases are executed.
     * Registers the primary stage and sets up the JavaFX application for testing.
     *
     * @throws Exception If an exception occurs during setup.
     */
    @BeforeAll
    public static void setupSpec() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(MainMerged.class);
    }

    /**
     * Set up before each individual test case is executed.
     * Waits for the JavaFX application to be ready before proceeding with the test.
     *
     * @throws Exception If an exception occurs during setup.
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Wait for the application to be ready
        // Wait for the application to be ready
        FxToolkit.setupFixture(() -> {
            WaitForAsyncUtils.waitForFxEvents();
        });
    }

    /**
     * Clean up after each individual test case is executed.
     * Hides the primary stage and releases keyboard and mouse buttons.
     *
     * @throws Exception If an exception occurs during cleanup.
     */
    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Test method to check table availability after one minute of reservation.
     *
     * @throws InterruptedException If the thread is interrupted during the test.
     */
    @Test
    public void testTableAvailabilityAfterOneMinute() throws InterruptedException {
        // Reserve a table first
        clickOn("#nameField").write("John Doe");
        clickOn("#timeField").write("3:00");
        clickOn("#tableNumberField").write("4");
        clickOn("#capacityField").write("2");
        clickOn("#reserveButton");

        // Add a delay to give the application time to process the reservation and update the table
        TimeUnit.SECONDS.sleep(1);

        // Check if the table is available initially
        assertFalse(lookup("#reservationTable").queryTableView().getItems()
                .stream()
                .anyMatch(item -> {
                    ReservationDisplay reservation = (ReservationDisplay) item;
                    return reservation.getName().get().equals("John Doe") &&
                            reservation.getTime().get().equals("3:00") &&
                            reservation.getTableNumber().get() == 4 &&
                            reservation.getCapacity().get() == 2 &&
                            reservation.getRemainingTime() > 0;
                }));

        // Wait for 1 minute
        Platform.runLater(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Check if the table is available again
        assertTrue(lookup("#reservationTable").queryTableView().getItems()
                .stream()
                .anyMatch(item -> {
                    ReservationDisplay reservation = (ReservationDisplay) item;
                    return reservation.getName().get().equals("John Doe") &&
                            reservation.getTime().get().equals("3:00") &&
                            reservation.getTableNumber().get() == 4 &&
                            reservation.getCapacity().get() == 2 &&
                            reservation.getRemainingTime() == 0;
                }));
        // Wait for the Platform.runLater to complete (1 minute)
        WaitForAsyncUtils.waitForFxEvents();
    }
}
