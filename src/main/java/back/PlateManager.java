package back;

import java.util.ArrayList;
import java.util.List;

/**
 * The `PlateManager` class is responsible for managing a collection of food plates.
 * It provides access to a list of all available plates and allows retrieval of plates by index.
 */
public class PlateManager {
    private static List<Plate> allPlates = new ArrayList<>();

    // Initialize the list of plates with some sample plates
    static {
        allPlates.add(new Plate("Spaghetti Carbonara", 0));
        allPlates.add(new Plate("Margherita Pizza", 1));
        allPlates.add(new Plate("Grilled Salmon", 2));
        allPlates.add(new Plate("Caesar Salad", 3));
        allPlates.add(new Plate("Beef Tacos", 4));
        allPlates.add(new Plate("Chocolate Fondue", 5));
    }

    /**
     * Retrieves a list of all available plates.
     *
     * @return A list of all available plates.
     */
    public static List<Plate> getAllPlates() {
        return allPlates;
    }

}
