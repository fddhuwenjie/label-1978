package com.student.dao;

import com.student.exception.DaoException;
import com.student.model.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserDao {
    User findByUsername(String username);
    User findById(int id);
    User findById(Connection conn, int id) throws SQLException;
    boolean create(User user);
    boolean updatePassword(int userId, String newPassword);
    boolean updateRole(int userId, String newRole);
    List<Map<String, Object>> findAllUsersInfo();
}
