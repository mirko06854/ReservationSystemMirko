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
}
