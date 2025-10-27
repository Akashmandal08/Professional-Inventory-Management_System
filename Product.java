/**
 * Abstract class representing a generic product in the inventory.
 * Defines common attributes and methods.
 */
public abstract class Product {
    private String name;
    private double price;
    private int quantity;
    private int id;
    private static int idCounter = 0;

    /**
     * Main constructor for creating a new product.
     * Auto-increments ID.
     */
    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.id = ++idCounter;
    }

    /**
     * Constructor for loading from a file.
     * Sets a specific ID and does NOT increment the counter.
     */
    public Product(int id, String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.id = id;
    }

    // Abstract methods
    public abstract String getType();
    
    /**
     * Returns the specific detail (e.g., warranty or expiry) for display.
     */
    public abstract String getSpecificDetail();

    /**
     * Returns the specific detail as an object for the edit dialog.
     */
    public abstract Object getSpecificDetailObject();

    /**
     * Updates the specific detail from the edit dialog.
     */
    public abstract void setSpecificDetailObject(Object detail);


    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getId() { return id; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Updates the ID counter to the highest loaded ID.
     */
    public static void updateIdCounter(int maxId) {
        if (maxId > idCounter) {
            idCounter = maxId;
        }
    }

    /**
     * Generates a string representation for saving to a file.
     */
    public String toCSVString() {
        // Format: ID,Type,Name,Price,Quantity
        return String.join(",",
                String.valueOf(id),
                getType(),
                getName(),
                String.valueOf(getPrice()),
                String.valueOf(getQuantity())
        );
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Price: $" + String.format("%.2f", price) + ", Quantity: " + quantity;
    }
}

