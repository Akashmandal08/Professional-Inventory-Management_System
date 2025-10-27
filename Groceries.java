/**
 * Represents a grocery product, extending the abstract Product class.
 */
public class Groceries extends Product {
    private String expirationDate;

    public Groceries(String name, double price, int quantity, String expirationDate) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
    }

    public Groceries(int id, String name, double price, int quantity, String expirationDate) {
        super(id, name, price, quantity);
        this.expirationDate = expirationDate;
    }

    @Override
    public String getType() {
        return "Groceries";
    }

    // --- Implemented Abstract Methods ---
    @Override
    public String getSpecificDetail() {
        return "Expires: " + expirationDate;
    }
    
    @Override
    public Object getSpecificDetailObject() {
        return expirationDate; // Return the string
    }

    @Override
    public void setSpecificDetailObject(Object detail) {
        // A real app should validate this date string
        this.expirationDate = detail.toString();
    }
    // --- End Implemented Methods ---

    // Getter and Setter
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    @Override
    public String toCSVString() {
        return super.toCSVString() + "," + expirationDate;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: " + getType() + ", Expires: " + expirationDate;
    }
}

