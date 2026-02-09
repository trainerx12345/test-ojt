# Professional User Management System

A modern, professional Java Swing CRUD application with MySQL database integration.

## Features

✨ **Complete CRUD Operations**
- Create new users
- Read/View all users
- Update existing users
- Delete users with confirmation

🎨 **Modern UI Design**
- Professional color scheme
- Responsive layout
- Hover effects on buttons
- Styled table with alternating row colors
- Search functionality
- Form validation

🔍 **Advanced Features**
- Real-time search by ID or name
- Table row selection auto-fills form
- Auto-refresh after operations
- Error handling with user-friendly messages
- Confirmation dialogs for destructive operations

## Database Setup

### 1. Create Database

```sql
CREATE DATABASE testdb;
USE testdb;
```

### 2. Create Users Table

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. Insert Sample Data (Optional)

```sql
INSERT INTO users (name) VALUES 
('John Doe'),
('Jane Smith'),
('Robert Johnson'),
('Emily Davis'),
('Michael Wilson');
```

## Prerequisites

- Java JDK 8 or higher
- MySQL Server 5.7 or higher
- MySQL Connector/J (JDBC Driver)

## Installation

### 1. Download MySQL Connector

Download the MySQL Connector/J from:
https://dev.mysql.com/downloads/connector/j/

### 2. Configure Database Connection

Update the database credentials in `App.java`:

```java
static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
static final String DB_USER = "root";
static final String DB_PASS = "your_password";
```

### 3. Compile the Application

```bash
javac -cp .:mysql-connector-java-8.0.XX.jar App.java
```

### 4. Run the Application

**Windows:**
```bash
java -cp .;mysql-connector-java-8.0.XX.jar App
```

**Mac/Linux:**
```bash
java -cp .:mysql-connector-java-8.0.XX.jar App
```

## Usage Guide

### Creating a User
1. Enter the user's name in the "Full Name" field
2. Click "Create User" button
3. The new user will appear in the table

### Updating a User
1. Click on a row in the table to select a user
2. Modify the name in the "Full Name" field
3. Click "Update User" button

### Deleting a User
1. Click on a row in the table to select a user
2. Click "Delete User" button
3. Confirm the deletion in the dialog

### Searching Users
1. Enter search term (name or ID) in the search field
2. Click "Search" button
3. Click "Refresh" to show all users again

## Project Structure

```
project/
│
├── App.java                          # Main application file
├── mysql-connector-java-8.0.XX.jar   # MySQL JDBC driver
└── README.md                         # This file
```

## UI Color Scheme

- **Primary**: #2980b9 (Blue)
- **Secondary**: #3498db (Light Blue)
- **Success**: #2ecc71 (Green)
- **Danger**: #e74c3c (Red)
- **Background**: #ecf0f1 (Light Gray)
- **Card**: #ffffff (White)

## Troubleshooting

### Connection Error
- Verify MySQL server is running
- Check database credentials
- Ensure database and table exist
- Verify MySQL Connector is in classpath

### ClassNotFoundException
- Ensure MySQL Connector JAR is in the same directory
- Include it in the classpath when running

### Table Not Found
- Run the database setup SQL scripts
- Verify table name is 'users'
- Check database name is 'testdb'

## Future Enhancements

- [ ] Add email and phone fields
- [ ] Implement data export (CSV, PDF)
- [ ] Add user authentication
- [ ] Implement pagination for large datasets
- [ ] Add data validation rules
- [ ] Dark mode theme
- [ ] Multi-language support

## License

This project is open source and available for educational purposes.

## Author

Created as a professional demonstration of Java Swing CRUD application development.
