package back;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The `PlateManager` class is responsible for managing a collection of food plates.
 * It provides access to a list of all available plates and allows retrieval of plates by index.
 */
public class PlateManager {
    private static final List<Plate> allPlates = new ArrayList<>();

    // Initialize the list of plates with some sample plates
    static {
        allPlates.add(new Plate("Spaghetti Carbonara"));
        allPlates.add(new Plate("Margherita Pizza"));
        allPlates.add(new Plate("Grilled Salmon"));
        allPlates.add(new Plate("Caesar Salad"));
        allPlates.add(new Plate("Beef Tacos"));
        allPlates.add(new Plate("Chocolate Fondue"));
    }

    /**
     * Retrieves a list of all available plates.
     *
     * @return A list of all available plates.
     */
    public static List<Plate> getAllPlates() {
        return allPlates;
    }

    /**
     * Helper method to check if all ordered plates in a reservation have been paid.
     *
     * @param reservation The reservation for which to check plate payments.
     * @return True if all plates have been paid; otherwise, false.
     */
    public static boolean areAllPlatesPaid(Reservation reservation) {
        Map<String, Integer> platesMap = reservation.getPlatesMap();
        for (int quantity : platesMap.values()) {
            if (quantity > 0) {
                return false; // At least one plate is not paid
            }
        }
        return true; // All plates are paid
    }

    public static void handlePlates(String plateName, Reservation reservation, HBox hBox, Label label, VBox vBox) {
        reservation.decrementPlateQuantity(plateName); // Decrement by 1

        int updatedQuantity = reservation.getPlatesMap().getOrDefault(plateName, 0);
        label.setText(plateName + ": " + updatedQuantity);

        if (updatedQuantity <= 0) {
            vBox.getChildren().remove(hBox);
            if (PlateManager.areAllPlatesPaid(reservation)) {
                Reservation.deleteEachReservation();
            }
        }
    }
}
