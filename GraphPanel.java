import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * A panel that displays various graphs based on the inventory data.
 * Requires JFreeChart and JCommon libraries.
 */
public class GraphPanel extends JPanel {

    private Inventory inventory;
    private JPanel chartContainer;

    public GraphPanel(Inventory inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // This container will hold the charts
        chartContainer = new JPanel(new GridLayout(1, 2, 10, 10)); // 1 row, 2 columns
        add(chartContainer, BorderLayout.CENTER);

        refreshGraphs();
    }

    /**
     * Re-generates and displays all charts.
     */
    public void refreshGraphs() {
        chartContainer.removeAll();
        
        // 1. Create Pie Chart
        JFreeChart pieChart = createCategoryPieChart();
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        chartContainer.add(pieChartPanel);

        // 2. Create Bar Chart
        JFreeChart barChart = createStockBarChart();
        ChartPanel barChartPanel = new ChartPanel(barChart);
        chartContainer.add(barChartPanel);

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    /**
     * Creates a Pie Chart showing product distribution by category.
     */
    private JFreeChart createCategoryPieChart() {
        // --- FIX WAS HERE ---
        // DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        // The <String> generic type is removed to work with older JFreeChart versions
        DefaultPieDataset dataset = new DefaultPieDataset();
        // --- END FIX ---
        
        Map<String, Long> categoryCounts = inventory.getCategoryCounts();
        
        if (categoryCounts.isEmpty()) {
            dataset.setValue("No Data", 100);
        } else {
            for (Map.Entry<String, Long> entry : categoryCounts.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }
        
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Inventory by Category",
            dataset,
            true,  // Include legend
            true,
            false
        );
        return pieChart;
    }

    /**
     * Creates a Bar Chart showing the top 5 most stocked products.
     */
    private JFreeChart createStockBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Product> topProducts = inventory.getTopNStockedProducts(5);

        if (topProducts.isEmpty()) {
            dataset.addValue(0, "Stock", "No Data");
        } else {
            for (Product product : topProducts) {
                // Use Name + ID for uniqueness in case of duplicate names
                String label = product.getName() + " (ID:" + product.getId() + ")";
                dataset.addValue(product.getQuantity(), "Stock", label);
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "Top 5 Most Stocked Products",
            "Product",
            "Quantity",
            dataset,
            PlotOrientation.VERTICAL,
            false, // No legend
            true,
            false
        );
        return barChart;
    }
}

