import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * A dedicated dialog for adding a new product or editing an existing one.
 * (This is a new file)
 */
public class ProductDialog extends JDialog {

    private JTextField nameField, priceField, quantityField, specificField;
    private JComboBox<String> typeComboBox;
    private JLabel specificLabel;
    
    private Product product; // Holds the product being edited, or the new one created
    private boolean confirmed = false;

    /**
     * Constructor for creating a new product.
     */
    public ProductDialog(JFrame parent) {
        super(parent, "Add New Product", true); // 'true' for modal
        this.product = null;
        initComponents();
    }

    /**
     * Constructor for editing an existing product.
     */
    public ProductDialog(JFrame parent, Product productToEdit) {
        super(parent, "Edit Product (ID: " + productToEdit.getId() + ")", true);
        this.product = productToEdit;
        initComponents();
        loadProductData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        typeComboBox = new JComboBox<>(new String[]{"Electronics", "Groceries"});
        typeComboBox.addActionListener(e -> updateSpecificField());
        formPanel.add(typeComboBox, gbc);

        // Row 1: Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Row 2: Price
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        priceField = new JTextField();
        formPanel.add(priceField, gbc);

        // Row 3: Quantity
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        quantityField = new JTextField();
        formPanel.add(quantityField, gbc);

        // Row 4: Specific Field
        gbc.gridx = 0; gbc.gridy = 4;
        specificLabel = new JLabel("Warranty (months):");
        formPanel.add(specificLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        specificField = new JTextField();
        formPanel.add(specificField, gbc);
        
        add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> onConfirm());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set initial state
        updateSpecificField();

        pack(); // Size the dialog based on its components
        setLocationRelativeTo(getParent()); // Center it
    }

    /**
     * Populates fields if editing an existing product.
     */
    private void loadProductData() {
        if (product == null) return;
        
        nameField.setText(product.getName());
        priceField.setText(String.valueOf(product.getPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        typeComboBox.setSelectedItem(product.getType());
        specificField.setText(product.getSpecificDetailObject().toString());

        // Disable type switching when editing
        typeComboBox.setEnabled(false); 
    }

    /**
     * Updates the label for the type-specific field.
     */
    private void updateSpecificField() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        if ("Electronics".equals(selectedType)) {
            specificLabel.setText("Warranty (months):");
        } else {
            specificLabel.setText("Expiration Date (YYYY-MM-DD):");
        }
    }

    /**
     * Validates and saves the data, then closes the dialog.
     */
    private void onConfirm() {
        // Validation
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            String type = (String) typeComboBox.getSelectedItem();

            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }
            if (price < 0 || quantity < 0) {
                throw new IllegalArgumentException("Price and Quantity cannot be negative.");
            }
            
            // If we are editing, update the existing product object
            if (this.product != null) {
                product.setName(name);
                product.setPrice(price);
                product.setQuantity(quantity);
                product.setSpecificDetailObject(specificField.getText());
            } 
            // If we are adding, create a new product object
            else {
                if ("Electronics".equals(type)) {
                    int warranty = Integer.parseInt(specificField.getText());
                    if (warranty < 0) throw new IllegalArgumentException("Warranty cannot be negative.");
                    this.product = new Electronics(name, price, quantity, warranty);
                } else {
                    String expiry = specificField.getText();
                    if (!expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        throw new IllegalArgumentException("Date format must be YYYY-MM-DD");
                    }
                    this.product = new Groceries(name, price, quantity, expiry);
                }
            }
            
            this.confirmed = true;
            dispose(); // Close the dialog
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number for price, quantity, or warranty.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        this.confirmed = false;
        dispose();
    }

    /**
     * Public method to show the dialog and get the result.
     * @return The new or updated Product, or null if cancelled.
     */
    public Product getProduct() {
        return this.confirmed ? this.product : null;
    }
}

