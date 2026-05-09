CREATE DATABASE IF NOT EXISTS student_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10),
    age INT,
    class_name VARCHAR(50),
    major VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 默认用户 - 初始使用明文密码，系统会自动兼容
-- 首次登录后建议修改密码，新密码将使用哈希存储
-- 或者通过应用启动时的初始化逻辑自动迁移
INSERT INTO users (username, password, email, role) VALUES 
('admin', 'admin123', 'admin@example.com', 'admin'),
('user', 'user123', 'user@example.com', 'user')
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO students (student_no, name, gender, age, class_name, major, phone, address) VALUES 
('2024001', '张三', '男', 20, '计算机1班', '计算机科学与技术', '13800138001', '北京市海淀区'),
('2024002', '李四', '女', 19, '计算机1班', '计算机科学与技术', '13800138002', '上海市浦东新区'),
('2024003', '王五', '男', 21, '软件2班', '软件工程', '13800138003', '广州市天河区')
ON DUPLICATE KEY UPDATE student_no=student_no;
