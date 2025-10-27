/**
 * Custom exception class that is thrown when a product is not found in the inventory.
 * (This file is unchanged from your original)
 */
public class ProductNotFoundException extends Exception {
    /**
     * Constructor for ProductNotFoundException.
     * @param message The detail message.
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}

