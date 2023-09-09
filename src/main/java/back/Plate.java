package back;

/**
 * The `Plate` class represents a food plate or dish.
 * It stores information about the name of the plate.
 */

public class Plate {
    private String name;

    /**
     * Constructs a `Plate` object with the specified name.
     *
     * @param name The name of the plate.
     * @param i The number of plates.
     */
    public Plate(String name, int i) {
        this.name = name;
    }

    /**
     * Gets the name of the plate.
     *
     * @return The name of the plate.
     */
    public String getName() {
        return name;
    }
}
