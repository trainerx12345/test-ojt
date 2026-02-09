-- ============================================
-- User Management System - Database Setup
-- ============================================

-- Create database
CREATE DATABASE IF NOT EXISTS testdb;
USE testdb;

-- Drop table if exists (for clean reinstall)
DROP TABLE IF EXISTS users;

-- Create users table with enhanced structure
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NULL,
    phone VARCHAR(20) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO users (name, email, phone) VALUES 
('John Doe', 'john.doe@email.com', '+1-555-0101'),
('Jane Smith', 'jane.smith@email.com', '+1-555-0102'),
('Robert Johnson', 'robert.j@email.com', '+1-555-0103'),
('Emily Davis', 'emily.davis@email.com', '+1-555-0104'),
('Michael Wilson', 'michael.w@email.com', '+1-555-0105'),
('Sarah Brown', 'sarah.brown@email.com', '+1-555-0106'),
('David Lee', 'david.lee@email.com', '+1-555-0107'),
('Lisa Anderson', 'lisa.anderson@email.com', '+1-555-0108'),
('James Taylor', 'james.taylor@email.com', '+1-555-0109'),
('Emma Martinez', 'emma.martinez@email.com', '+1-555-0110');

-- Verify data
SELECT * FROM users;

-- Display table structure
DESCRIBE users;

-- Show total count
SELECT COUNT(*) as total_users FROM users;
