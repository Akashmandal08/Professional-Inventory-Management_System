import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the collection of products in the inventory.
 * Provides methods to add, remove, update, find, and save/load products.
 * Includes logic for graph data.
 */
public class Inventory {
    private List<Product> products;
    private static final String SAVE_FILE = "inventory.csv";

    public Inventory() {
        products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(int productId) throws ProductNotFoundException {
        Product productToRemove = findProductById(productId);
        products.remove(productToRemove);
    }

    public Product findProductById(int productId) throws ProductNotFoundException {
        for (Product product : products) {
            if (product.getId() == productId) {
                return product;
            }
        }
        throw new ProductNotFoundException("Product with ID " + productId + " not found.");
    }
    
    /**
     * Updates an existing product's details using a new Product object.
     * This is a cleaner way to update.
     * @param updatedProduct The product object containing all new data (name, price, qty, specific details).
     * @throws ProductNotFoundException if the product is not found.
     */
    public void updateProduct(Product updatedProduct) throws ProductNotFoundException {
        Product product = findProductById(updatedProduct.getId());
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setQuantity(updatedProduct.getQuantity());
        product.setSpecificDetailObject(updatedProduct.getSpecificDetailObject());
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    // --- DASHBOARD & GRAPH METHODS ---

    public double getTotalInventoryValue() {
        return products.stream()
                       .mapToDouble(p -> p.getPrice() * p.getQuantity())
                       .sum();
    }

    public long getLowStockCount(int threshold) {
        return products.stream()
                       .filter(p -> p.getQuantity() <= threshold)
                       .count();
    }

    /**
     * Gets a map of product counts by their type (for Pie Chart).
     */
    public Map<String, Long> getCategoryCounts() {
        return products.stream()
                       .collect(Collectors.groupingBy(Product::getType, Collectors.counting()));
    }

    /**
     * Gets a list of the top N most stocked products (for Bar Chart).
     * @param n The number of products to return.
     * @return A list of the top N products, sorted by quantity descending.
     */
    public List<Product> getTopNStockedProducts(int n) {
        return products.stream()
                       .sorted(Comparator.comparingInt(Product::getQuantity).reversed())
                       .limit(n)
                       .collect(Collectors.toList());
    }

    // --- DATA PERSISTENCE (SAVING/LOADING) ---

    /**
     * Saves the current inventory to a CSV file.
     * @throws IOException if a file writing error occurs.
     */
    public void saveToFile() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(SAVE_FILE))) {
            out.println("ID,Type,Name,Price,Quantity,SpecificDetail");
            for (Product product : products) {
                out.println(product.toCSVString());
            }
        }
    }

    /**
     * Loads the inventory from a CSV file.
     * @throws IOException if a file reading error occurs (except FileNotFound).
     */
    public void loadFromFile() throws IOException {
        products.clear();
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            br.readLine(); // Skip header row

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length < 5) continue; // Skip malformed lines

                try {
                    int id = Integer.parseInt(values[0]);
                    String type = values[1];
                    String name = values[2];
                    double price = Double.parseDouble(values[3]);
                    int quantity = Integer.parseInt(values[4]);
                    String specificDetail = (values.length > 5) ? values[5] : "";

                    Product product = null;
                    if ("Electronics".equals(type)) {
                        int warranty = specificDetail.isEmpty() ? 0 : Integer.parseInt(specificDetail);
                        product = new Electronics(id, name, price, quantity, warranty);
                    } else if ("Groceries".equals(type)) {
                        product = new Groceries(id, name, price, quantity, specificDetail);
                    }

                    if (product != null) {
                        products.add(product);
                        if (id > maxId) {
                            maxId = id; // Track the highest ID
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Skipping malformed line: " + line);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            // This is fine, just means no save file exists yet.
            System.out.println("No save file found. Starting fresh.");
            return; // Exit method, no need to update counter
        }
        // IMPORTANT: Update the static ID counter in Product class
        Product.updateIdCounter(maxId);
    }
}

