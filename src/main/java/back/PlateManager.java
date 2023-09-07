package back;

import java.util.ArrayList;
import java.util.List;

public class PlateManager {
    // Create a list to store available plates
    private static List<Plate> allPlates = new ArrayList<>();

    // Initialize the list with some sample plates (you can add more as needed)
    static {
        allPlates.add(new Plate("Spaghetti Carbonara"));
        allPlates.add(new Plate("Margherita Pizza"));
        allPlates.add(new Plate("Grilled Salmon"));
        allPlates.add(new Plate("Caesar Salad"));
        allPlates.add(new Plate("Beef Tacos"));
        allPlates.add(new Plate("Chocolate Fondue"));

    }

    // Get the list of all available plates
    public static List<Plate> getAllPlates() {
        return allPlates;
    }
}

