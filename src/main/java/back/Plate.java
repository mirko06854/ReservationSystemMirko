package back;

public class Plate {

    private static String name;
    private static int quantity;

    public Plate(String name) {
        this.name = name;
    }

    public Plate(String name, int quantity) {
        this.name = name;
    }

    public static String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
