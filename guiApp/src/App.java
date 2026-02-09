import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class App {
    // Database credentials
    static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    static final String DB_USER = "root"; 
    static final String DB_PASS = ""; 

    public static void main(String[] args) {
        // Create the Frame
        JFrame frame = new JFrame("MySQL CRUD Application");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Top Panel - Input Fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        
        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("Create");
        JButton readBtn = new JButton("Read All");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        
        buttonPanel.add(createBtn);
        buttonPanel.add(readBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        // Text Area for displaying results
        JTextArea textArea = new JTextArea(15, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Add components to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // CREATE - Insert new record
        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a name!");
                    return;
                }
                
                try {
                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    String sql = "INSERT INTO users (name) VALUES (?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "User created successfully!");
                        nameField.setText("");
                        // Auto-refresh the display
                        readBtn.doClick();
                    }
                    
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        // READ - Fetch all records
        readBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM users");

                    StringBuilder results = new StringBuilder();
                    results.append("=== USER LIST ===\n\n");
                    
                    boolean hasData = false;
                    while (rs.next()) {
                        hasData = true;
                        results.append("ID: ").append(rs.getInt("id"))
                               .append(" | Name: ").append(rs.getString("name"))
                               .append("\n");
                    }
                    
                    if (!hasData) {
                        results.append("No records found.");
                    }
                    
                    textArea.setText(results.toString());
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        // UPDATE - Modify existing record
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idText = idField.getText().trim();
                String name = nameField.getText().trim();
                
                if (idText.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both ID and Name!");
                    return;
                }
                
                try {
                    int id = Integer.parseInt(idText);
                    
                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    String sql = "UPDATE users SET name = ? WHERE id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    pstmt.setInt(2, id);
                    
                    int rowsAffected = pstmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "User updated successfully!");
                        idField.setText("");
                        nameField.setText("");
                        readBtn.doClick();
                    } else {
                        JOptionPane.showMessageDialog(frame, "No user found with ID: " + id);
                    }
                    
                    conn.close();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(frame, "ID must be a number!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        // DELETE - Remove record
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idText = idField.getText().trim();
                
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter an ID to delete!");
                    return;
                }
                
                try {
                    int id = Integer.parseInt(idText);
                    
                    int confirm = JOptionPane.showConfirmDialog(
                        frame, 
                        "Are you sure you want to delete user with ID: " + id + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                        String sql = "DELETE FROM users WHERE id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setInt(1, id);
                        
                        int rowsAffected = pstmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "User deleted successfully!");
                            idField.setText("");
                            nameField.setText("");
                            readBtn.doClick();
                        } else {
                            JOptionPane.showMessageDialog(frame, "No user found with ID: " + id);
                        }
                        
                        conn.close();
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(frame, "ID must be a number!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        // CLEAR - Clear input fields
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                idField.setText("");
                nameField.setText("");
            }
        });

        // Load data on startup
        frame.setVisible(true);
        readBtn.doClick();
    }
}