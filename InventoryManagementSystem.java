import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// --- FIXES ARE HERE ---
import java.awt.event.InputEvent; // Added this import
import java.util.Map;              // Added this import
// --- END FIXES ---


/**
 * The main class for the Inventory Management System.
 * Features a modern UI with FlatLaf, a tabbed interface,
 * a menu bar, toolbar, graphs, and background tasks.
 * (This file is heavily modified)
 */
public class InventoryManagementSystem extends JFrame {

    private Inventory inventory = new Inventory();
    private DefaultTableModel tableModel;
    private JTable productTable;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private JLabel statusBarLabel;
    private GraphPanel graphPanel;
    private JTabbedPane tabbedPane;
    
    // Dashboard components
    private JLabel totalValueLabel, totalItemsLabel, lowStockLabel;
    private JPanel categoryStatsPanel;

    public InventoryManagementSystem() {
        setTitle("Inventory Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // --- Create Main Components ---
        setJMenuBar(createMenuBar());
        add(createToolBar(), BorderLayout.NORTH);
        add(createStatusBar(), BorderLayout.SOUTH);
        
        tabbedPane = new JTabbedPane();
        
        // --- 1. Inventory List Tab ---
        tabbedPane.addTab("Inventory List", createInventoryPanel());
        
        // --- 2. Dashboard Tab ---
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        
        // --- 3. Graphs Tab ---
        graphPanel = new GraphPanel(inventory);
        tabbedPane.addTab("Graphs", graphPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // --- Listen for tab changes to refresh graphs/dashboard ---
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent == graphPanel) {
                graphPanel.refreshGraphs();
            } else if (selectedComponent == dashboardPanel) { // Assuming dashboardPanel is a class field
                refreshDashboard();
            }
        });
        
        // --- Load Data ---
        loadDataWithWorker();
    }
    
    // Declare dashboardPanel as a class field
    private JPanel dashboardPanel; 

    /**
     * Creates the main menu bar.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // --- File Menu ---
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem saveItem = new JMenuItem("Save", UIManager.getIcon("FileView.floppyDriveIcon"));
        // --- FIX WAS HERE ---
        // Changed ActionEvent.CTRL_DOWN_MASK to InputEvent.CTRL_DOWN_MASK
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        // --- END FIX ---
        saveItem.addActionListener(e -> saveDataWithWorker());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // --- View Menu (for L&F) ---
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JRadioButtonMenuItem lightMode = new JRadioButtonMenuItem("Light Mode", true);
        JRadioButtonMenuItem darkMode = new JRadioButtonMenuItem("Dark Mode", false);
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightMode);
        themeGroup.add(darkMode);
        
        lightMode.addActionListener(e -> changeLookFeel(new FlatLightLaf()));
        darkMode.addActionListener(e -> changeLookFeel(new FlatDarkLaf()));
        
        viewMenu.add(lightMode);
        viewMenu.add(darkMode);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        return menuBar;
    }

    /**
     * Creates the main toolbar with icon buttons.
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMargin(new Insets(5, 5, 5, 5));

        // Using built-in icons for a professional feel
        JButton addButton = new JButton("Add", UIManager.getIcon("FileChooser.newFolderIcon")); 
        addButton.setToolTipText("Add New Product (Ctrl+N)");
        addButton.addActionListener(e -> showAddProductDialog());
        // Map Ctrl+N to the add action
        // --- FIX WAS HERE ---
        getRootPane().registerKeyboardAction(e -> showAddProductDialog(),
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        // --- END FIX ---

        JButton editButton = new JButton("Edit", UIManager.getIcon("Actions.edit")); 
        editButton.setToolTipText("Edit Selected Product (Ctrl+E)");
        editButton.addActionListener(e -> showUpdateProductDialog());
        // --- FIX WAS HERE ---
        getRootPane().registerKeyboardAction(e -> showUpdateProductDialog(),
            KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        // --- END FIX ---

        JButton deleteButton = new JButton("Delete", UIManager.getIcon("InternalFrame.closeIcon"));
        deleteButton.setToolTipText("Delete Selected Product (Delete)");
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        getRootPane().registerKeyboardAction(e -> deleteSelectedProduct(),
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        return toolBar;
    }

    /**
     * Creates the bottom status bar.
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBarLabel = new JLabel("Ready.");
        statusBarLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusBar.add(statusBarLabel, BorderLayout.CENTER);
        return statusBar;
    }

    /**
     * Creates the main panel for the inventory list and search.
     */
    private JPanel createInventoryPanel() {
        JPanel inventoryPanel = new JPanel(new BorderLayout(5, 5));
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(new JLabel("Search by Name: "), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        inventoryPanel.add(searchPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Name", "Price", "Quantity", "Type", "Details"};
        tableModel = new DefaultTableModel(columnNames, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
               return false; // Make table cells not editable
             }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Sorting and Filtering (Search)
        sorter = new TableRowSorter<>(tableModel);
        productTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void insertUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    // Filter by Name (column 1), case-insensitive
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                }
            }
        });

        inventoryPanel.add(scrollPane, BorderLayout.CENTER);
        return inventoryPanel;
    }

    /**
     * Creates the dashboard panel with key metrics.
     */
    private JPanel createDashboardPanel() {
        // We use the class field here
        dashboardPanel = new JPanel(); 
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Inventory Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardPanel.add(titleLabel);
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Key Metrics Panel ---
        JPanel metricsPanel = new JPanel(new GridLayout(3, 1, 0, 10)); // 3 rows, 1 col
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Key Metrics"));
        metricsPanel.setMaximumSize(new Dimension(400, 150)); // Constrain size
        metricsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalValueLabel = new JLabel("Total Inventory Value: $0.00");
        totalValueLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        totalItemsLabel = new JLabel("Total Product Lines: 0");
        totalItemsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        lowStockLabel = new JLabel("Items Low on Stock (<= 10): 0");
        lowStockLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        metricsPanel.add(totalValueLabel);
        metricsPanel.add(totalItemsLabel);
        metricsPanel.add(lowStockLabel);
        
        dashboardPanel.add(metricsPanel);
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Category Stats Panel ---
        categoryStatsPanel = new JPanel();
        categoryStatsPanel.setLayout(new BoxLayout(categoryStatsPanel, BoxLayout.Y_AXIS));
        categoryStatsPanel.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));
        categoryStatsPanel.setMaximumSize(new Dimension(400, 200)); // Constrain size
        categoryStatsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        dashboardPanel.add(categoryStatsPanel);
        dashboardPanel.add(Box.createVerticalGlue()); // Push to top
        
        return dashboardPanel;
    }


    /**
     * Refreshes table, graphs, and dashboard.
     */
    private void refreshAllData() {
        refreshTable();
        refreshDashboard();
        
        // Only refresh graphs if the panel exists
        if (graphPanel != null) {
            graphPanel.refreshGraphs();
        }
    }

    /**
     * (Re)populates the JTable with data from the inventory.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Product> products = inventory.getAllProducts();
        for (Product product : products) {
            Object[] row = {
                    product.getId(),
                    product.getName(),
                    String.format("%.2f", product.getPrice()),
                    product.getQuantity(),
                    product.getType(),
                    product.getSpecificDetail()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Refreshes all the labels on the dashboard panel.
     */
    private void refreshDashboard() {
        if (totalValueLabel == null) return; // Don't refresh if UI not built yet

        // 1. Refresh Key Metrics
        totalValueLabel.setText(String.format("Total Inventory Value: $%.2f", inventory.getTotalInventoryValue()));
        totalItemsLabel.setText("Total Product Lines: " + inventory.getAllProducts().size());
        lowStockLabel.setText("Items Low on Stock (<= 10): " + inventory.getLowStockCount(10));

        // 2. Refresh Category Stats
        categoryStatsPanel.removeAll(); // Clear old stats
        
        // --- FIX WAS HERE ---
        // Added Map import at top of file
        Map<String, Long> counts = inventory.getCategoryCounts();
        // --- END FIX ---
        int totalProducts = inventory.getAllProducts().size();
        
        if (totalProducts == 0) {
            categoryStatsPanel.add(new JLabel("No products in inventory."));
        } else {
            // --- FIX WAS HERE ---
            // Added Map import at top of file
            for (Map.Entry<String, Long> entry : counts.entrySet()) {
            // --- END FIX ---
                String category = entry.getKey();
                long count = entry.getValue();
                double percentage = (double) count / totalProducts * 100.0;
                
                String statText = String.format("%s: %d products (%.1f%%)", category, count, percentage);
                JLabel statLabel = new JLabel(statText);
                statLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                categoryStatsPanel.add(statLabel);
                categoryStatsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        categoryStatsPanel.revalidate();
        categoryStatsPanel.repaint();
    }


    // --- Action Handlers ---

    private void showAddProductDialog() {
        ProductDialog dialog = new ProductDialog(this);
        dialog.setVisible(true);
        
        Product newProduct = dialog.getProduct(); // Will be null if cancelled
        if (newProduct != null) {
            inventory.addProduct(newProduct);
            refreshAllData();
            statusBarLabel.setText("Added product: " + newProduct.getName());
        }
    }
    
    private void showUpdateProductDialog() {
        int selectedViewRow = productTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", "No Product Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert view row to model row in case of sorting
        int modelRow = productTable.convertRowIndexToModel(selectedViewRow);
        int productId = (int) tableModel.getValueAt(modelRow, 0);

        try {
            Product productToEdit = inventory.findProductById(productId);
            
            ProductDialog dialog = new ProductDialog(this, productToEdit);
            dialog.setVisible(true);
            
            Product updatedProduct = dialog.getProduct(); // Will be null if cancelled
            if (updatedProduct != null) {
                // The dialog has modified the productToEdit object
                inventory.updateProduct(updatedProduct); 
                refreshAllData();
                statusBarLabel.setText("Updated product ID: " + updatedProduct.getId());
            }
        } catch (ProductNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int selectedViewRow = productTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "No Product Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = productTable.convertRowIndexToModel(selectedViewRow);
        int productId = (int) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete '" + productName + "' (ID: " + productId + ")?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                inventory.removeProduct(productId);
                refreshAllData();
                statusBarLabel.setText("Deleted product ID: " + productId);
            } catch (ProductNotFoundException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Changes the application's Look and Feel.
     */
    private void changeLookFeel(LookAndFeel laf) {
        try {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set Look and Feel.");
        }
    }

    // --- Background Tasks (SwingWorker) ---

    private void loadDataWithWorker() {
        statusBarLabel.setText("Loading inventory...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    inventory.loadFromFile();
                    return "Inventory loaded successfully.";
                } catch (IOException e) {
                    return "Error loading file: " + e.getMessage();
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    statusBarLabel.setText(result);
                } catch (Exception e) {
                    statusBarLabel.setText("Failed to load data: " + e.getMessage());
                }
                refreshAllData();
                setCursor(Cursor.getDefaultCursor());
            }
        };
        worker.execute();
    }
    
    private void saveDataWithWorker() {
        statusBarLabel.setText("Saving inventory...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    inventory.saveToFile();
                    return "Inventory saved successfully.";
                } catch (IOException e) {
                    return "Error saving file: " + e.getMessage();
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    statusBarLabel.setText(result);
                } catch (Exception e) {
                    statusBarLabel.setText("Failed to save data: " + e.getMessage());
                }
                setCursor(Cursor.getDefaultCursor());
            }
        };
        worker.execute();
    }


    /**
     * Main method. Sets up the FlatLaf Look and Feel.
     */
    public static void main(String[] args) {
        // Set up the modern Look and Feel *before* creating the GUI
        try {
            // Use FlatLightLaf by default
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf. Using default Java L&F.");
        }
        
        // Ensure GUI creation is on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new InventoryManagementSystem().setVisible(true);
        });
    }
}

