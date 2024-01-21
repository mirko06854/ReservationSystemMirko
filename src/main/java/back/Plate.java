package back;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The `Plate` class represents a food plate or dish.
 * It stores information about the name of the plate.
 */

public record Plate(String name) {
    /**
     * Constructs a `Plate` object with the specified name.
     *
     * @param name The name of the plate.
     */
    public Plate {
    }

    /**
     * Gets the name of the plate.
     *
     * @return The name of the plate.
     */
    @Override
    public String name() {
        return name;
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
