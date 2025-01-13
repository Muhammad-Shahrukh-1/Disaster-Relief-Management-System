
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Main {

    // File paths for storing data
    private static final String DISASTERS_FILE = "disasters.txt";
    private static final String VOLUNTEERS_FILE = "volunteers.txt";
    private static final String VICTIMS_FILE = "victims.txt";
    private static final String DONATIONS_FILE = "donations.txt";
    private static final String RESOURCES_FILE = "resources.txt";

    public static void main(String[] args) {
        // Apply Nimbus Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize Nimbus Look and Feel");
        }

        // Initialize required files
        initializeFiles();

        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("NDMA Disaster Management System");
            mainFrame.setSize(800, 600);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLayout(new BorderLayout());
            mainFrame.getContentPane().setBackground(new Color(240, 240, 240));

            // Header Panel
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(52, 152, 219));
            JLabel titleLabel = new JLabel("NDMA Disaster Management System");
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerPanel.add(titleLabel);
            mainFrame.add(headerPanel, BorderLayout.NORTH);

            // Home Panel
            JPanel homePanel = new JPanel(new GridLayout(2, 3, 15, 15));
            homePanel.setBackground(new Color(240, 240, 240));
            homePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // Feature Buttons
            JButton disastersButton = createModernButton("Track Disasters");
            JButton volunteersButton = createModernButton("Track Volunteers");
            JButton victimsButton = createModernButton("Track Victims");
            JButton donationsButton = createModernButton("Track Donations");
            JButton resourcesButton = createModernButton("Track Resources");

            homePanel.add(disastersButton);
            homePanel.add(volunteersButton);
            homePanel.add(victimsButton);
            homePanel.add(donationsButton);
            homePanel.add(resourcesButton);
            mainFrame.add(homePanel, BorderLayout.CENTER);

            // Button Actions
            disastersButton.addActionListener(e -> new DisastersThread().start());
            volunteersButton.addActionListener(e -> new GenericWindowThread("Volunteers").start());
            victimsButton.addActionListener(e -> new GenericWindowThread("Victims").start());
            donationsButton.addActionListener(e -> new GenericWindowThread("Donations").start());
            resourcesButton.addActionListener(e -> new GenericWindowThread("Resources").start());

            mainFrame.setVisible(true);
        });
    }

    // Initialize files if they don't exist
    private static void initializeFiles() {
        String[] files = { DISASTERS_FILE, VOLUNTEERS_FILE, VICTIMS_FILE, DONATIONS_FILE, RESOURCES_FILE };
        for (String file : files) {
            File f = new File(file);
            if (!f.exists()) {
                try {
                    if (f.createNewFile()) {
                        System.out.println("File created: " + file);
                    }
                } catch (IOException e) {
                    System.err.println("Error creating file: " + file);
                }
            }
        }
    }

    // Modern Button Styling
    private static JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        return button;
    }

    // Show Generic Window for Data with Generics
    @SuppressWarnings("unchecked")
    private static <T> void showGenericWindow(String title, List<T> dataList, String[] columnNames, String filePath) {
        JFrame frame = new JFrame(title);
        frame.setSize(600, 400);

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (T data : dataList) {
            String[] rowData = (String[]) data; // Casting to String array for display
            model.addRow(rowData);
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createModernButton("Add");
        JButton updateButton = createModernButton("Update");
        JButton deleteButton = createModernButton("Delete");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions to add, update, or delete data
        addButton.addActionListener(e -> {
            String[] newRow = new String[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                newRow[i] = JOptionPane.showInputDialog("Enter " + columnNames[i] + ":");
            }
            dataList.add((T) newRow); // Cast to generic type
            model.addRow(newRow);
            saveData(filePath, dataList);
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                for (int i = 0; i < columnNames.length; i++) {
                    String newValue = JOptionPane.showInputDialog("Update " + columnNames[i] + ":",
                            table.getValueAt(selectedRow, i));
                    table.setValueAt(newValue, selectedRow, i);
                    ((String[]) dataList.get(selectedRow))[i] = newValue;
                }
                saveData(filePath, dataList);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                dataList.remove(selectedRow);
                model.removeRow(selectedRow);
                saveData(filePath, dataList);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to delete.");
            }
        });

        frame.setVisible(true);
    }

    // Load Data from File
    private static List<String[]> loadData(String filePath) {
        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                dataList.add(parts); // No casting needed since parts is already of type String[]
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
        }
        return dataList;
    }

    // Save Data back to File
    private static <T> void saveData(String filePath, List<T> dataList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (T row : dataList) {
                writer.write(String.join(",", (String[]) row) + "\n"); // Cast to String array
            }
        } catch (IOException e) {
            System.err.println("Error saving data");
        }
    }

    // Disasters Thread
    static class DisastersThread extends Thread {
        @Override
        public void run() {
            List<String[]> disasters = loadData(DISASTERS_FILE);
            String[] columnNames = { "Disaster ID", "Name", "Location", "Description", "Status" };
            showGenericWindow("Disasters", disasters, columnNames, DISASTERS_FILE);
        }
    }

    // Generic Window Thread
    static class GenericWindowThread extends Thread {
        private String title;

        public GenericWindowThread(String title) {
            this.title = title;
        }

        @Override
        public void run() {
            if (title.equals("Volunteers")) {
                List<String[]> volunteers = loadData(VOLUNTEERS_FILE);
                String[] columnNames = { "ID", "Name", "Contact" };
                showGenericWindow("Volunteers", volunteers, columnNames, VOLUNTEERS_FILE);
            } else if (title.equals("Victims")) {
                List<String[]> victims = loadData(VICTIMS_FILE);
                String[] columnNames = { "ID", "Name", "Location", "Status" };
                showGenericWindow("Victims", victims, columnNames, VICTIMS_FILE);
            } else if (title.equals("Donations")) {
                List<String[]> donations = loadData(DONATIONS_FILE);
                String[] columnNames = { "ID", "Amount", "Donor", "Date" };
                showGenericWindow("Donations", donations, columnNames, DONATIONS_FILE);
            } else if (title.equals("Resources")) {
                List<String[]> resources = loadData(RESOURCES_FILE);
                String[] columnNames = { "ID", "Resource", "Quantity", "Location" };
                showGenericWindow("Resources", resources, columnNames, RESOURCES_FILE);
            }
        }
    }
}
