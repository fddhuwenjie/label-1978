package com.student.servlet;

import com.student.dao.SessionDao;
import com.student.dao.SessionDaoImpl;
import com.student.dao.UserDao;
import com.student.dao.UserDaoImpl;
import com.student.model.Session;
import com.student.model.User;
import com.student.util.JsonUtil;
import com.student.config.AppConfig;
import com.student.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthServlet extends HttpServlet {
    private UserDao userDao;
    private SessionDao sessionDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoImpl();
        sessionDao = new SessionDaoImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        switch (path) {
            case "/login":
                handleLogin(req, resp);
                break;
            case "/register":
                handleRegister(req, resp);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                JsonUtil.writeError(resp, 404, "接口不存在");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> body = JsonUtil.fromJson(req, Map.class);
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            JsonUtil.writeError(resp, 400, "用户名和密码不能为空");
            return;
        }

        User user = userDao.findByUsername(username);
        if (user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
            JsonUtil.writeError(resp, 401, "用户名或密码错误");
            return;
        }

        // Create session
        String sessionId = UUID.randomUUID().toString();
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + AppConfig.SESSION_TIMEOUT_SECONDS * 1000);
        Session session = new Session(sessionId, user.getId(), expiresAt);
        sessionDao.create(session);

        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", sessionId);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("expiresIn", AppConfig.SESSION_TIMEOUT_SECONDS);

        JsonUtil.writeSuccess(resp, data);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> body = JsonUtil.fromJson(req, Map.class);
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");

        if (username == null || password == null) {
            JsonUtil.writeError(resp, 400, "用户名和密码不能为空");
            return;
        }

        if (userDao.findByUsername(username) != null) {
            JsonUtil.writeError(resp, 400, "用户名已存在");
            return;
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(username, hashedPassword, email, "user");
        if (userDao.create(user)) {
            JsonUtil.writeSuccess(resp, "注册成功");
        } else {
            JsonUtil.writeError(resp, 500, "注册失败");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = req.getHeader("X-Session-Id");
        if (sessionId != null) {
            sessionDao.delete(sessionId);
        }
        JsonUtil.writeSuccess(resp, "已退出登录");
    }
}
