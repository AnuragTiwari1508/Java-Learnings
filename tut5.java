import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// Person class - same as before
class PersonGUI {
    private int number;
    private String name;
    private double salary;
    private String occupation;
    
    public PersonGUI(int number, String name, double salary, String occupation) {
        this.number = number;
        this.name = name;
        this.salary = salary;
        this.occupation = occupation;
    }
    
    public int getNumber() { return number; }
    public String getName() { return name; }
    public double getSalary() { return salary; }
    public String getOccupation() { return occupation; }
}

public class tut5 extends JFrame {
    private ArrayList<PersonGUI> people;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JTextArea detailsArea;
    
    public tut5() {
        people = new ArrayList<>();
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Person Management System - GUI Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);
        
        // Create main panels
        createTopPanel();
        createCenterPanel();
        createBottomPanel();
        
        // Center the window
        setLocationRelativeTo(null);
        
        // Apply some styling
        getContentPane().setBackground(new Color(240, 248, 255));
    }
    
    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("PERSON MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JButton addPersonBtn = new JButton("Add New Person");
        addPersonBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addPersonBtn.setBackground(new Color(50, 205, 50));
        addPersonBtn.setForeground(Color.WHITE);
        addPersonBtn.addActionListener(e -> showAddPersonDialog());
        
        JButton clearAllBtn = new JButton("Clear All");
        clearAllBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearAllBtn.setBackground(new Color(220, 20, 60));
        clearAllBtn.setForeground(Color.WHITE);
        clearAllBtn.addActionListener(e -> clearAllData());
        
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(addPersonBtn);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(clearAllBtn);
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table
        String[] columns = {"ID", "Name", "Salary ($)", "Occupation"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(176, 196, 222));
        table.setSelectionBackground(new Color(173, 216, 230));
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("All People"));
        
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Person"));
        
        searchField = new JTextField(10);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton searchBtn = new JButton("Search by ID");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        searchBtn.setBackground(new Color(30, 144, 255));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> searchPerson());
        
        searchPanel.add(new JLabel("Enter ID (e.g., A12): "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        
        // Details panel
        detailsArea = new JTextArea(8, 30);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(248, 248, 255));
        detailsArea.setText("Search results will appear here...");
        
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Person Details"));
        
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        bottomPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void showAddPersonDialog() {
        JDialog dialog = new JDialog(this, "Add New Person", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Form fields
        JTextField numberField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField salaryField = new JTextField(15);
        JTextField occupationField = new JTextField(15);
        
        // Add form components
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        dialog.add(new JLabel("ID Number:"), gbc);
        gbc.gridx = 1;
        dialog.add(numberField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Salary ($):"), gbc);
        gbc.gridx = 1;
        dialog.add(salaryField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Occupation:"), gbc);
        gbc.gridx = 1;
        dialog.add(occupationField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Person");
        JButton cancelBtn = new JButton("Cancel");
        
        addBtn.setBackground(new Color(50, 205, 50));
        addBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(new Color(220, 20, 60));
        cancelBtn.setForeground(Color.WHITE);
        
        addBtn.addActionListener(e -> {
            try {
                int number = Integer.parseInt(numberField.getText().trim());
                String name = nameField.getText().trim();
                double salary = Double.parseDouble(salaryField.getText().trim());
                String occupation = occupationField.getText().trim();
                
                if (name.isEmpty() || occupation.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if ID already exists
                for (PersonGUI person : people) {
                    if (person.getNumber() == number) {
                        JOptionPane.showMessageDialog(dialog, "ID A" + number + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                PersonGUI newPerson = new PersonGUI(number, name, salary, occupation);
                people.add(newPerson);
                updateTable();
                
                JOptionPane.showMessageDialog(dialog, "Person added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for ID and Salary!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        
        dialog.setVisible(true);
    }
    
    private void updateTable() {
        tableModel.setRowCount(0); // Clear existing rows
        
        for (PersonGUI person : people) {
            Object[] row = {
                "A" + person.getNumber(),
                person.getName(),
                String.format("%.2f", person.getSalary()),
                person.getOccupation()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchPerson() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID to search!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Remove 'A' prefix if present
            String numberStr = searchText.startsWith("A") ? searchText.substring(1) : searchText;
            int searchNumber = Integer.parseInt(numberStr);
            
            PersonGUI foundPerson = null;
            for (PersonGUI person : people) {
                if (person.getNumber() == searchNumber) {
                    foundPerson = person;
                    break;
                }
            }
            
            if (foundPerson != null) {
                String details = String.format(
                    "=======================================\n" +
                    "        PERSON DETAILS - A%d\n" +
                    "=======================================\n" +
                    "Number        : A%d\n" +
                    "Name          : %s\n" +
                    "Salary        : $%.2f\n" +
                    "Occupation    : %s\n" +
                    "=======================================",
                    foundPerson.getNumber(),
                    foundPerson.getNumber(),
                    foundPerson.getName(),
                    foundPerson.getSalary(),
                    foundPerson.getOccupation()
                );
                
                detailsArea.setText(details);
                
                // Highlight the row in table
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (table.getValueAt(i, 0).toString().equals("A" + searchNumber)) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        break;
                    }
                }
                
            } else {
                detailsArea.setText("❌ Person with number A" + searchNumber + " not found!\n\nAvailable IDs:\n" + getAvailableIds());
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ID number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getAvailableIds() {
        StringBuilder ids = new StringBuilder();
        for (PersonGUI person : people) {
            ids.append("A").append(person.getNumber()).append(" ");
        }
        return ids.toString();
    }
    
    private void clearAllData() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all data?", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            people.clear();
            updateTable();
            detailsArea.setText("All data cleared. Add new people to get started...");
            searchField.setText("");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new tut5().setVisible(true);
        });
    }
}
