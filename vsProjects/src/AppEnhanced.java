import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppEnhanced extends JFrame {
    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    static final String DB_USER = "root";
    static final String DB_PASS = "";

    // UI Components
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, emailField, phoneField, searchField;
    private JButton createBtn, updateBtn, deleteBtn, clearBtn, refreshBtn, searchBtn, exportBtn;
    private JLabel statusLabel, recordCountLabel;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color CARD_COLOR = Color.WHITE;

    public AppEnhanced() {
        initializeUI();
        loadTableData();
    }

    private void initializeUI() {
        setTitle("User Management System Pro");
        setSize(1200, 750);
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

        // Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Title Section
        JLabel titleLabel = new JLabel("User Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Complete CRUD Operations with Professional Interface");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        titleContainer.setBackground(PRIMARY_COLOR);
        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        // Stats Panel
        recordCountLabel = new JLabel("Total Users: 0");
        recordCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        recordCountLabel.setForeground(Color.WHITE);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        headerPanel.add(recordCountLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(BACKGROUND_COLOR);

        // Toolbar Panel
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBackground(CARD_COLOR);
        toolbarPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Search Section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(CARD_COLOR);

        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        searchField = new JTextField(25);
        styleTextField(searchField);
        searchField.setToolTipText("Search by ID, name, email, or phone");
        
        searchBtn = createStyledButton("Search", PRIMARY_COLOR);
        searchBtn.setToolTipText("Search users");
        
        refreshBtn = createStyledButton("↻ Refresh", SECONDARY_COLOR);
        refreshBtn.setToolTipText("Reload all users");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        // Export Section
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        exportPanel.setBackground(CARD_COLOR);

        exportBtn = createStyledButton("📊 Export Data", SUCCESS_COLOR);
        exportBtn.setToolTipText("Export to text file");

        exportPanel.add(exportBtn);

        toolbarPanel.add(searchPanel, BorderLayout.WEST);
        toolbarPanel.add(exportPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Name", "Email", "Phone", "Created"};
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
                emailField.setText(table.getValueAt(selectedRow, 2) != null ? 
                    table.getValueAt(selectedRow, 2).toString() : "");
                phoneField.setText(table.getValueAt(selectedRow, 3) != null ? 
                    table.getValueAt(selectedRow, 3).toString() : "");
                updateStatus("Selected user: " + table.getValueAt(selectedRow, 1));
            }
        });

        // Double-click to edit
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        nameField.requestFocus();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tablePanel.add(toolbarPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        searchBtn.addActionListener(e -> searchUsers());
        refreshBtn.addActionListener(e -> {
            loadTableData();
            searchField.setText("");
            updateStatus("Data refreshed");
        });
        exportBtn.addActionListener(e -> exportData());

        // Enter key on search field
        searchField.addActionListener(e -> searchUsers());

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
        formPanel.setPreferredSize(new Dimension(380, 0));

        // Form Title
        JLabel formTitle = new JLabel("📝 User Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formTitle);
        
        JLabel formSubtitle = new JLabel("Fill in the information below");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formSubtitle.setForeground(new Color(127, 140, 141));
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(formSubtitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ID Field
        idField = new JTextField();
        formPanel.add(createFieldPanel("User ID", idField, "Auto-generated"));
        idField.setEditable(false);
        idField.setBackground(new Color(236, 240, 241));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Name Field
        nameField = new JTextField();
        formPanel.add(createFieldPanel("Full Name *", nameField, "Enter user's full name"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Email Field
        emailField = new JTextField();
        formPanel.add(createFieldPanel("Email Address", emailField, "user@example.com"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Phone Field
        phoneField = new JTextField();
        formPanel.add(createFieldPanel("Phone Number", phoneField, "+1234567890"));
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Required field note
        JLabel requiredNote = new JLabel("* Required field");
        requiredNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        requiredNote.setForeground(DANGER_COLOR);
        requiredNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(requiredNote);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        buttonsPanel.setBackground(CARD_COLOR);
        buttonsPanel.setMaximumSize(new Dimension(330, 190));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createBtn = createStyledButton("✚ Create User", SUCCESS_COLOR);
        updateBtn = createStyledButton("✎ Update User", PRIMARY_COLOR);
        deleteBtn = createStyledButton("🗑 Delete User", DANGER_COLOR);
        clearBtn = createStyledButton("⟲ Clear Form", new Color(149, 165, 166));

        createBtn.setPreferredSize(new Dimension(330, 42));
        updateBtn.setPreferredSize(new Dimension(330, 42));
        deleteBtn.setPreferredSize(new Dimension(330, 42));
        clearBtn.setPreferredSize(new Dimension(330, 42));

        createBtn.setToolTipText("Add a new user to the database");
        updateBtn.setToolTipText("Update the selected user's information");
        deleteBtn.setToolTipText("Delete the selected user");
        clearBtn.setToolTipText("Clear all form fields");

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

    private JPanel createFieldPanel(String labelText, JTextField textField, String tooltip) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(330, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        styleTextField(textField);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setToolTipText(tooltip);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(textField);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel versionLabel = new JLabel("Version 2.0");
        versionLabel.setForeground(new Color(189, 195, 199));
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setSelectionBackground(new Color(52, 152, 219, 80));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(236, 240, 241));
        table.setIntercellSpacing(new Dimension(1, 1));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }

    // CRUD Operations
    private void createUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty()) {
            showError("Name is required!");
            nameField.requestFocus();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO users (name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email.isEmpty() ? null : email);
            pstmt.setString(3, phone.isEmpty() ? null : phone);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showSuccess("User created successfully!");
                updateStatus("New user added: " + name);
                clearForm();
                loadTableData();
            }
        } catch (Exception ex) {
            showError("Error creating user: " + ex.getMessage());
            updateStatus("Error: Failed to create user");
        }
    }

    private void updateUser() {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (idText.isEmpty()) {
            showError("Please select a user to update!");
            return;
        }

        if (name.isEmpty()) {
            showError("Name is required!");
            nameField.requestFocus();
            return;
        }

        try {
            int id = Integer.parseInt(idText);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, email.isEmpty() ? null : email);
                pstmt.setString(3, phone.isEmpty() ? null : phone);
                pstmt.setInt(4, id);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    showSuccess("User updated successfully!");
                    updateStatus("User updated: " + name);
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
            updateStatus("Error: Failed to update user");
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
            String userName = nameField.getText();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + userName + "\"?\nThis action cannot be undone.",
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
                        updateStatus("User deleted: " + userName);
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
            updateStatus("Error: Failed to delete user");
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY id DESC");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                row.add(timestamp != null ? sdf.format(timestamp) : "N/A");
                
                tableModel.addRow(row);
            }

            recordCountLabel.setText("Total Users: " + tableModel.getRowCount());
            updateStatus("Loaded " + tableModel.getRowCount() + " user(s)");
        } catch (Exception ex) {
            showError("Error loading data: " + ex.getMessage());
            updateStatus("Error: Failed to load data");
        }
    }

    private void searchUsers() {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            loadTableData();
            return;
        }

        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM users WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? OR id = ? ORDER BY id DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try {
                pstmt.setInt(4, Integer.parseInt(searchText));
            } catch (NumberFormatException e) {
                pstmt.setInt(4, -1);
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            int count = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                row.add(timestamp != null ? sdf.format(timestamp) : "N/A");
                
                tableModel.addRow(row);
                count++;
            }

            if (count == 0) {
                showInfo("No users found matching: \"" + searchText + "\"");
                updateStatus("No results found");
            } else {
                updateStatus("Found " + count + " user(s) matching: \"" + searchText + "\"");
            }
        } catch (Exception ex) {
            showError("Error searching: " + ex.getMessage());
            updateStatus("Error: Search failed");
        }
    }

    private void exportData() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export User Data");
            fileChooser.setSelectedFile(new java.io.File("users_export.txt"));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                
                try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave)) {
                    writer.println("USER MANAGEMENT SYSTEM - DATA EXPORT");
                    writer.println("Export Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    writer.println("Total Records: " + tableModel.getRowCount());
                    writer.println("=" .repeat(80));
                    writer.println();

                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        writer.println("User #" + (i + 1));
                        writer.println("  ID: " + tableModel.getValueAt(i, 0));
                        writer.println("  Name: " + tableModel.getValueAt(i, 1));
                        writer.println("  Email: " + tableModel.getValueAt(i, 2));
                        writer.println("  Phone: " + tableModel.getValueAt(i, 3));
                        writer.println("  Created: " + tableModel.getValueAt(i, 4));
                        writer.println("-".repeat(80));
                    }

                    showSuccess("Data exported successfully to:\n" + fileToSave.getAbsolutePath());
                    updateStatus("Data exported to: " + fileToSave.getName());
                }
            }
        } catch (Exception ex) {
            showError("Error exporting data: " + ex.getMessage());
            updateStatus("Error: Export failed");
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        table.clearSelection();
        nameField.requestFocus();
        updateStatus("Form cleared");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message + " - " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
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
            AppEnhanced app = new AppEnhanced();
            app.setVisible(true);
        });
    }
}
