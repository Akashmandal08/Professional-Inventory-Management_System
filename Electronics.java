/**
 * Represents an electronics product, extending the abstract Product class.
 */
public class Electronics extends Product {
    private int warrantyPeriod; // in months

    public Electronics(String name, double price, int quantity, int warrantyPeriod) {
        super(name, price, quantity);
        this.warrantyPeriod = warrantyPeriod;
    }

    public Electronics(int id, String name, double price, int quantity, int warrantyPeriod) {
        super(id, name, price, quantity);
        this.warrantyPeriod = warrantyPeriod;
    }

    @Override
    public String getType() {
        return "Electronics";
    }

    // --- Implemented Abstract Methods ---
    @Override
    public String getSpecificDetail() {
        return "Warranty: " + warrantyPeriod + " months";
    }

    @Override
    public Object getSpecificDetailObject() {
        return warrantyPeriod; // Return the integer
    }

    @Override
    public void setSpecificDetailObject(Object detail) {
        try {
            this.warrantyPeriod = Integer.parseInt(detail.toString());
        } catch (NumberFormatException e) {
            this.warrantyPeriod = 0; // Default on error
        }
    }
    // --- End Implemented Methods ---

    // Getter and Setter
    public int getWarrantyPeriod() { return warrantyPeriod; }
    public void setWarrantyPeriod(int warrantyPeriod) { this.warrantyPeriod = warrantyPeriod; }

    @Override
    public String toCSVString() {
        return super.toCSVString() + "," + warrantyPeriod;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: " + getType() + ", Warranty: " + warrantyPeriod + " months";
    }
}

