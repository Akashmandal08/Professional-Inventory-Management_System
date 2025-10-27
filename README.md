# Professional Inventory Management System 🧥🍎💻

A comprehensive Java Swing desktop application for managing product inventory, featuring a modern user interface, data persistence, dashboard analytics, and graphical representations.

---

## 📖 Description

This project provides a robust solution for tracking inventory items. It allows users to add, edit, delete, and view products, categorized primarily into **Electronics** and **Groceries**, each with specific attributes (warranty and expiration date, respectively). The system features a clean, tabbed interface for easy navigation between the inventory list, a dashboard displaying key metrics, and graphical visualizations of inventory data. Data is saved locally in a CSV file (`inventory.csv`).

---

## ✨ Features

* **Modern UI**: Utilizes the FlatLaf library for a clean, modern look and feel (Light and Dark themes available).
* **CRUD Operations**: Full **C**reate, **R**ead, **U**pdate, and **D**elete functionality for inventory products.
* **Product Types**: Supports different product types (Electronics, Groceries) with specific details (Warranty Period, Expiration Date).
* **Tabbed Interface**: Organizes functionality into distinct tabs:
    * **Inventory List**: Displays all products in a sortable, filterable table.
    * **Dashboard**: Shows key metrics like total inventory value, total items, low stock count, and a category breakdown.
    * **Graphs**: Visualizes inventory data using JFreeChart (Pie chart for category distribution, Bar chart for top stocked items).
* **Search/Filtering**: Allows users to filter the inventory list by product name (case-insensitive).
* **Data Persistence**: Saves and loads inventory data to/from a local CSV file (`inventory.csv`).
* **Error Handling**: Includes custom exception (`ProductNotFoundException`) for better error management.
* **Background Tasks**: Uses `SwingWorker` for non-blocking saving and loading operations, keeping the UI responsive.
* **Toolbar & Menu Bar**: Provides quick access to common actions (Add, Edit, Delete, Save, Theme Change, Exit) via toolbar buttons and menu items.
* **Keyboard Shortcuts**: Includes shortcuts for common actions (e.g., Ctrl+N for Add, Ctrl+E for Edit, Ctrl+S for Save, Delete key for Delete).
* **Status Bar**: Displays feedback messages to the user.

---

## 🛠️ Technologies Used

* **Java**: Core programming language.
* **Java Swing**: GUI toolkit for building the desktop interface.
* **FlatLaf**: Modern look and feel library for Swing applications.
* **JFreeChart**: Library for creating charts and graphs.
* **JCommon**: Required dependency for JFreeChart.
* **CSV**: Simple text format for data persistence.

---

## 📂 Project Structure
. ├── InventoryManagementSystem.java # Main application class, GUI setup ├── Inventory.java # Manages the collection of products, load/save logic ├── Product.java # Abstract base class for products ├── Electronics.java # Concrete product class for Electronics ├── Groceries.java # Concrete product class for Groceries ├── ProductDialog.java # Dialog for adding/editing products ├── GraphPanel.java # JPanel containing JFreeChart graphs ├── ProductNotFoundException.java # Custom exception class ├── inventory.csv # Data storage file (created/updated automatically) └── lib/ # Folder containing external libraries ├── flatlaf-3.4.1.jar # FlatLaf Look and Feel library ├── jcommon-1.0.23.jar # JCommon library (for JFreeChart) └── jfreechart-1.0.19.jar # JFreeChart library

---

## 🚀 Getting Started

Follow these steps to get the project running on your local machine.

### Prerequisites

* **Java Development Kit (JDK)**: Version 8 or higher installed. You can download it from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use an open-source distribution like [OpenJDK](https://openjdk.java.net/).
* **Git**: For cloning the repository.
* **(Optional) IDE**: An Integrated Development Environment like IntelliJ IDEA, Eclipse, or VS Code with Java extensions is recommended for easier compilation and running.

### Cloning the Repository

1.  Open your terminal or command prompt.
2.  Navigate to the directory where you want to clone the project.
3.  Run the following command (replace `<repository-url>` with the actual URL of your Git repository):

    ```bash
    git clone <repository-url>
    cd professional-inventory-management_system
    ```

### Setup & Running

There are two main ways to compile and run the project:

**Method 1: Using an IDE (Recommended)**

1.  **Open Project**: Open your IDE (IntelliJ, Eclipse, VSCode).
2.  **Import Project**: Use the IDE's import function to open the cloned `professional-inventory-management_system` directory as a Java project.
    * **IntelliJ**: `File -> Open...` and select the project directory.
    * **Eclipse**: `File -> Import... -> General -> Existing Projects into Workspace` and select the project directory.
3.  **Configure Libraries**: Ensure the JAR files in the `lib` directory (`flatlaf-3.4.1.jar`, `jcommon-1.0.23.jar`, `jfreechart-1.0.19.jar`) are added to the project's build path or classpath.
    * **IntelliJ**: Right-click the `lib` folder -> `Add as Library...`.
    * **Eclipse**: Right-click the project -> `Build Path -> Configure Build Path... -> Libraries tab -> Add JARs...` and select the JARs in the `lib` folder.
4.  **Run**: Locate the `InventoryManagementSystem.java` file, right-click it, and select "Run" or "Debug".

**Method 2: Using Command Line**

1.  **Open Terminal/Command Prompt**: Navigate to the root directory of the cloned project (`professional-inventory-management_system`).
2.  **Compile**: Compile all the Java source files, including the libraries in the classpath. Use the appropriate path separator (`:` for Linux/macOS, `;` for Windows).

    * **Linux/macOS:**
        ```bash
        javac -cp ".:lib/flatlaf-3.4.1.jar:lib/jcommon-1.0.23.jar:lib/jfreechart-1.0.19.jar" *.java
        ```
    * **Windows:**
        ```bash
        javac -cp ".;lib/flatlaf-3.4.1.jar;lib/jcommon-1.0.23.jar;lib/jfreechart-1.0.19.jar" *.java
        ```
    *(Note: If you encounter issues compiling `*.java`, you might need to specify each `.java` file individually or ensure your `javac` version handles wildcards correctly.)*

3.  **Run**: Execute the main class, again including the libraries in the classpath.

    * **Linux/macOS:**
        ```bash
        java -cp ".:lib/flatlaf-3.4.1.jar:lib/jcommon-1.0.23.jar:lib/jfreechart-1.0.19.jar" InventoryManagementSystem
        ```
    * **Windows:**
        ```bash
        java -cp ".;lib/flatlaf-3.4.1.jar;lib/jcommon-1.0.23.jar;lib/jfreechart-1.0.19.jar" InventoryManagementSystem
        ```

---

## 🕹️ Usage

1.  **Launch**: Run the application using one of the methods above.
2.  **Inventory List Tab**:
    * View all current inventory items.
    * Click column headers to sort the table.
    * Use the search bar at the top to filter items by name.
    * Select a row to enable the "Edit" and "Delete" actions.
3.  **Toolbar/Menu Actions**:
    * **Add (Ctrl+N)**: Opens a dialog to add a new Electronics or Groceries product. Fill in the details and click "OK".
    * **Edit (Ctrl+E)**: Select a product in the table and click this to open the edit dialog. Modify details and click "OK". The product type cannot be changed once created.
    * **Delete (Delete Key)**: Select a product and click this (or press Delete). A confirmation prompt will appear.
    * **Save (Ctrl+S)**: Manually save the current inventory state to `inventory.csv`. This also happens automatically if data exists when loading fails initially (e.g., first run).
    * **View Menu**: Change between Light and Dark themes.
    * **Exit**: Closes the application.
4.  **Dashboard Tab**: View summarized statistics about your inventory. Refreshes automatically when data changes or the tab is selected.
5.  **Graphs Tab**: View graphical representations (Pie and Bar charts) of the inventory. Refreshes automatically when data changes or the tab is selected.
6.  **Status Bar**: Check the bottom bar for messages about loading, saving, adding, editing, or deleting items.

---

## 💾 Data Persistence

* The inventory is saved in a comma-separated value (CSV) file named `inventory.csv` located in the same directory where the application is run.
* The file includes a header row: `ID,Type,Name,Price,Quantity,SpecificDetail`.
* Data is automatically loaded when the application starts.
* Data can be manually saved using the "File -> Save" menu item or Ctrl+S.
* If `inventory.csv` is not found on startup, the application starts with an empty inventory.

---

## 🔗 Dependencies

The project relies on the following external libraries, which are included in the `lib` folder:

1.  **FlatLaf** ([https://www.formdev.com/flatlaf/](https://www.formdev.com/flatlaf/)): For the modern look and feel. (Version 3.4.1 used)
2.  **JFreeChart** ([http://www.jfree.org/jfreechart/](http://www.jfree.org/jfreechart/)): For generating charts. (Version 1.0.19 used)
3.  **JCommon** ([http://www.jfree.org/jcommon/](http://www.jfree.org/jcommon/)): A required dependency for JFreeChart. (Version 1.0.23 used)

These `.jar` files **must** be included in the classpath when compiling and running the application.

---

## 🤝 Contributing

Contributions are welcome! If you'd like to contribute, please fork the repository and submit a pull request. For major changes, please open an issue first to discuss what you would like to change.

---

## 📜 License

This project is currently unlicensed. Please contact the author for licensing information.
