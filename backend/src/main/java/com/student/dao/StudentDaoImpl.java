package com.student.dao;

import com.student.exception.DaoException;
import com.student.model.Student;
import com.student.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDaoImpl implements StudentDao {
    private static final Logger logger = LoggerFactory.getLogger(StudentDaoImpl.class);

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("查询学生列表失败", e);
        }
        return students;
    }

    @Override
    public Student findById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("查询学生失败", e);
        }
        return null;
    }

    @Override
    public Student findByStudentNo(String studentNo) {
        String sql = "SELECT * FROM students WHERE student_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("查询学生失败", e);
        }
        return null;
    }

    @Override
    public boolean create(Student student) {
        String sql = "INSERT INTO students (student_no, name, gender, age, class_name, major, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, student.getStudentNo());
                ps.setString(2, student.getName());
                ps.setString(3, student.getGender());
                ps.setInt(4, student.getAge() != null ? student.getAge() : 0);
                ps.setString(5, student.getClassName());
                ps.setString(6, student.getMajor());
                ps.setString(7, student.getPhone());
                ps.setString(8, student.getAddress());
                boolean result = ps.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database operation failed", e);
            throw new DaoException("添加学生失败", e);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET student_no=?, name=?, gender=?, age=?, class_name=?, major=?, phone=?, address=? WHERE id=?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, student.getStudentNo());
                ps.setString(2, student.getName());
                ps.setString(3, student.getGender());
                ps.setInt(4, student.getAge() != null ? student.getAge() : 0);
                ps.setString(5, student.getClassName());
                ps.setString(6, student.getMajor());
                ps.setString(7, student.getPhone());
                ps.setString(8, student.getAddress());
                ps.setInt(9, student.getId());
                boolean result = ps.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database operation failed", e);
            throw new DaoException("更新学生失败", e);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                boolean result = ps.executeUpdate() > 0;
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database operation failed", e);
            throw new DaoException("删除学生失败", e);
        } finally {
            close(conn);
        }
    }

    @Override
    public List<Student> search(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? OR student_no LIKE ? OR class_name LIKE ? ORDER BY id DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapStudent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("搜索学生失败", e);
        }
        return students;
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentNo(rs.getString("student_no"));
        student.setName(rs.getString("name"));
        student.setGender(rs.getString("gender"));
        student.setAge(rs.getInt("age"));
        student.setClassName(rs.getString("class_name"));
        student.setMajor(rs.getString("major"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        student.setUpdatedAt(rs.getTimestamp("updated_at"));
        return student;
    }
}
