package com.student.dao;

import com.student.model.Session;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public interface SessionDao {
    boolean create(Session session);
    Session findBySessionId(String sessionId);
    Session findBySessionId(Connection conn, String sessionId) throws SQLException;
    boolean delete(String sessionId);
    boolean refresh(String sessionId, Timestamp newExpiresAt);
    boolean refresh(Connection conn, String sessionId, Timestamp newExpiresAt) throws SQLException;
    void cleanExpired();
}
