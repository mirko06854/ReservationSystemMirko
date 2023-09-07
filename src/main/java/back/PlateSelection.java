package back;

public class PlateSelection {
    private Plate plate;
    private int quantity;

    public PlateSelection(Plate plate, int quantity) {
        this.plate = plate;
        this.quantity = quantity;
    }

    public Plate getPlate() {
        return plate;
    }

    public int getQuantity() {
        return quantity;
    }
}

