package com.student.dao;

import com.student.exception.DaoException;
import com.student.model.Session;
import com.student.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class SessionDaoImpl implements SessionDao {
    private static final Logger logger = LoggerFactory.getLogger(SessionDaoImpl.class);

    @Override
    public boolean create(Session session) {
        String sql = "INSERT INTO sessions (session_id, user_id, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, session.getSessionId());
            ps.setInt(2, session.getUserId());
            ps.setTimestamp(3, session.getExpiresAt());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("创建会话失败", e);
        }
    }

    @Override
    public Session findBySessionId(String sessionId) {
        try (Connection conn = DBUtil.getConnection()) {
            return findBySessionId(conn, sessionId);
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("查询会话失败", e);
        }
    }

    @Override
    public Session findBySessionId(Connection conn, String sessionId) throws SQLException {
        String sql = "SELECT * FROM sessions WHERE session_id = ? AND expires_at > NOW()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session session = new Session();
                    session.setId(rs.getInt("id"));
                    session.setSessionId(rs.getString("session_id"));
                    session.setUserId(rs.getInt("user_id"));
                    session.setCreatedAt(rs.getTimestamp("created_at"));
                    session.setExpiresAt(rs.getTimestamp("expires_at"));
                    return session;
                }
            }
        }
        return null;
    }

    @Override
    public boolean delete(String sessionId) {
        String sql = "DELETE FROM sessions WHERE session_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("删除会话失败", e);
        }
    }

    @Override
    public boolean refresh(String sessionId, Timestamp newExpiresAt) {
        try (Connection conn = DBUtil.getConnection()) {
            return refresh(conn, sessionId, newExpiresAt);
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            throw new DaoException("刷新会话失败", e);
        }
    }

    @Override
    public boolean refresh(Connection conn, String sessionId, Timestamp newExpiresAt) throws SQLException {
        String sql = "UPDATE sessions SET expires_at = ? WHERE session_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, newExpiresAt);
            ps.setString(2, sessionId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public void cleanExpired() {
        String sql = "DELETE FROM sessions WHERE expires_at < NOW()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
        }
    }
}
