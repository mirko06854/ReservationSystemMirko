package back;

import java.util.ArrayList;
import java.util.List;

public class PlateManager {
    private static List<Plate> allPlates = new ArrayList<>();

    static {
        allPlates.add(new Plate("Spaghetti Carbonara", 0));
        allPlates.add(new Plate("Margherita Pizza", 1));
        allPlates.add(new Plate("Grilled Salmon", 2));
        allPlates.add(new Plate("Caesar Salad", 3));
        allPlates.add(new Plate("Beef Tacos", 4));
        allPlates.add(new Plate("Chocolate Fondue", 5));
    }

    public static List<Plate> getAllPlates() {
        return allPlates;
    }

    // Add a method to get a specific plate by index
    public static Plate getPlateByIndex(int index) {
        if (index >= 0 && index < allPlates.size()) {
            return allPlates.get(index);
        }
        return null;
    }
}
