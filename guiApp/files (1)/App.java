import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class App extends JFrame {
    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    static final String DB_USER = "root";
    static final String DB_PASS = "";

    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, searchField;
    private JButton createBtn, updateBtn, deleteBtn, clearBtn, refreshBtn, searchBtn;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color CARD_COLOR = Color.WHITE;

    public App() {
        initializeUI();
        loadTableData();
    }

    private void initializeUI() {
        setTitle("User Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(15, 15));

        // Add padding around the main content
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Center Panel with Table
        add(createTablePanel(), BorderLayout.CENTER);

        // Right Panel with Form
        add(createFormPanel(), BorderLayout.EAST);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("User Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage your users efficiently");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        titleContainer.setBackground(PRIMARY_COLOR);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        headerPanel.add(titleContainer, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(BACKGROUND_COLOR);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(CARD_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        searchField = new JTextField(20);
        styleTextField(searchField);
        
        searchBtn = createStyledButton("Search", PRIMARY_COLOR);
        refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        // Table
        String[] columnNames = {"ID", "Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);

        // Table selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                idField.setText(table.getValueAt(selectedRow, 0).toString());
                nameField.setText(table.getValueAt(selectedRow, 1).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        searchBtn.addActionListener(e -> searchUsers());
        refreshBtn.addActionListener(e -> loadTableData());

        return tablePanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        formPanel.setPreferredSize(new Dimension(350, 0));

        // Form Title
        JLabel formTitle = new JLabel("User Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ID Field
        formPanel.add(createFieldPanel("User ID", idField = new JTextField()));
        idField.setEditable(false);
        idField.setBackground(new Color(236, 240, 241));
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Name Field
        formPanel.add(createFieldPanel("Full Name", nameField = new JTextField()));
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        buttonsPanel.setBackground(CARD_COLOR);
        buttonsPanel.setMaximumSize(new Dimension(300, 180));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createBtn = createStyledButton("Create User", SUCCESS_COLOR);
        updateBtn = createStyledButton("Update User", PRIMARY_COLOR);
        deleteBtn = createStyledButton("Delete User", DANGER_COLOR);
        clearBtn = createStyledButton("Clear Form", new Color(149, 165, 166));

        createBtn.setPreferredSize(new Dimension(300, 40));
        updateBtn.setPreferredSize(new Dimension(300, 40));
        deleteBtn.setPreferredSize(new Dimension(300, 40));
        clearBtn.setPreferredSize(new Dimension(300, 40));

        buttonsPanel.add(createBtn);
        buttonsPanel.add(updateBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(clearBtn);

        formPanel.add(buttonsPanel);
        formPanel.add(Box.createVerticalGlue());

        // Action Listeners
        createBtn.addActionListener(e -> createUser());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());
        clearBtn.addActionListener(e -> clearForm());

        return formPanel;
    }

    private JPanel createFieldPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        styleTextField(textField);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(textField);

        return panel;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(236, 240, 241));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);

        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }

    // CRUD Operations
    private void createUser() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter a name!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO users (name) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showSuccess("User created successfully!");
                clearForm();
                loadTableData();
            }
        } catch (Exception ex) {
            showError("Error creating user: " + ex.getMessage());
        }
    }

    private void updateUser() {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();

        if (idText.isEmpty() || name.isEmpty()) {
            showError("Please select a user and enter a name!");
            return;
        }

        try {
            int id = Integer.parseInt(idText);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "UPDATE users SET name = ? WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setInt(2, id);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    showSuccess("User updated successfully!");
                    clearForm();
                    loadTableData();
                } else {
                    showError("No user found with ID: " + id);
                }
            }
        } catch (NumberFormatException nfe) {
            showError("Invalid ID format!");
        } catch (Exception ex) {
            showError("Error updating user: " + ex.getMessage());
        }
    }

    private void deleteUser() {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            showError("Please select a user to delete!");
            return;
        }

        try {
            int id = Integer.parseInt(idText);

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    String sql = "DELETE FROM users WHERE id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, id);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        showSuccess("User deleted successfully!");
                        clearForm();
                        loadTableData();
                    } else {
                        showError("No user found with ID: " + id);
                    }
                }
            }
        } catch (NumberFormatException nfe) {
            showError("Invalid ID format!");
        } catch (Exception ex) {
            showError("Error deleting user: " + ex.getMessage());
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY id DESC");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            showError("Error loading data: " + ex.getMessage());
        }
    }

    private void searchUsers() {
        String searchText = searchField.getText().trim();
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = searchText.isEmpty() 
                ? "SELECT * FROM users ORDER BY id DESC"
                : "SELECT * FROM users WHERE name LIKE ? OR id = ? ORDER BY id DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            if (!searchText.isEmpty()) {
                pstmt.setString(1, "%" + searchText + "%");
                try {
                    pstmt.setInt(2, Integer.parseInt(searchText));
                } catch (NumberFormatException e) {
                    pstmt.setInt(2, -1);
                }
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                showInfo("No users found matching your search.");
            }
        } catch (Exception ex) {
            showError("Error searching: " + ex.getMessage());
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        table.clearSelection();
    }

    // Message dialogs with custom styling
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}
