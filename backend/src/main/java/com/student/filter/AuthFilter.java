package com.student.filter;

import com.student.config.AppConfig;
import com.student.dao.SessionDao;
import com.student.dao.SessionDaoImpl;
import com.student.dao.UserDao;
import com.student.dao.UserDaoImpl;
import com.student.model.Session;
import com.student.model.User;
import com.student.util.DBUtil;
import com.student.util.JsonUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

public class AuthFilter implements Filter {
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/auth/logout"
    );

    private SessionDao sessionDao;
    private UserDao userDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionDao = new SessionDaoImpl();
        userDao = new UserDaoImpl();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();
        
        for (String publicPath : PUBLIC_PATHS) {
            if (path.endsWith(publicPath) || path.contains(publicPath)) {
                chain.doFilter(request, response);
                return;
            }
        }

        String sessionId = req.getHeader("X-Session-Id");
        if (sessionId == null || sessionId.isEmpty()) {
            JsonUtil.writeError(res, 401, "未登录，请先登录");
            return;
        }

        // 使用单个连接完成所有数据库操作
        try (Connection conn = DBUtil.getConnection()) {
            Session session = sessionDao.findBySessionId(conn, sessionId);
            if (session == null) {
                JsonUtil.writeError(res, 401, "登录已过期，请重新登录");
                return;
            }

            Timestamp newExpiry = new Timestamp(System.currentTimeMillis() + AppConfig.SESSION_TIMEOUT_SECONDS * 1000);
            sessionDao.refresh(conn, sessionId, newExpiry);

            User user = userDao.findById(conn, session.getUserId());
            if (user != null) {
                req.setAttribute("currentUser", user);
                req.setAttribute("userId", user.getId());
                req.setAttribute("userRole", user.getRole());
            }
        } catch (SQLException e) {
            JsonUtil.writeError(res, 500, "系统错误");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
