package com.student.servlet;

import com.student.dao.UserDao;
import com.student.dao.UserDaoImpl;
import com.student.model.User;
import com.student.util.JsonUtil;
import com.student.util.PasswordUtil;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/info") || path.equals("/profile")) {
            handleGetProfile(req, resp);
        } else if (path.equals("/list")) {
            handleListUsers(req, resp);
        } else {
            JsonUtil.writeError(resp, 404, "接口不存在");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "";

        if (path.equals("/password")) {
            handleChangePassword(req, resp);
        } else if (path.equals("/role")) {
            handleUpdateRole(req, resp);
        } else {
            JsonUtil.writeError(resp, 404, "接口不存在");
        }
    }

    private void handleGetProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getAttribute("currentUser");
        if (user == null) {
            JsonUtil.writeError(resp, 401, "未登录");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("role", user.getRole());
        data.put("createdAt", user.getCreatedAt());

        JsonUtil.writeSuccess(resp, data);
    }

    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getAttribute("currentUser");
        if (user == null) {
            JsonUtil.writeError(resp, 401, "未登录");
            return;
        }

        Map<String, String> body = JsonUtil.fromJson(req, Map.class);
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            JsonUtil.writeError(resp, 400, "原密码和新密码不能为空");
            return;
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            JsonUtil.writeError(resp, 400, "原密码错误");
            return;
        }

        String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
        if (userDao.updatePassword(user.getId(), hashedNewPassword)) {
            JsonUtil.writeSuccess(resp, "密码修改成功");
        } else {
            JsonUtil.writeError(resp, 500, "密码修改失败");
        }
    }

    /**
     * 获取用户列表（仅管理员）
     */
    private void handleListUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = (User) req.getAttribute("currentUser");
        if (currentUser == null) {
            JsonUtil.writeError(resp, 401, "未登录");
            return;
        }

        if (!"admin".equals(currentUser.getRole())) {
            JsonUtil.writeError(resp, 403, "无权限，仅管理员可查看用户列表");
            return;
        }

        List<Map<String, Object>> users = userDao.findAllUsersInfo();
        JsonUtil.writeSuccess(resp, users);
    }

    /**
     * 更新用户角色（仅管理员）
     */
    private void handleUpdateRole(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User currentUser = (User) req.getAttribute("currentUser");
        if (currentUser == null) {
            JsonUtil.writeError(resp, 401, "未登录");
            return;
        }

        if (!"admin".equals(currentUser.getRole())) {
            JsonUtil.writeError(resp, 403, "无权限，仅管理员可修改用户角色");
            return;
        }

        Map<String, Object> body = JsonUtil.fromJson(req, Map.class);
        Integer userId = body.get("userId") != null ? ((Number) body.get("userId")).intValue() : null;
        String newRole = (String) body.get("role");

        if (userId == null || newRole == null) {
            JsonUtil.writeError(resp, 400, "用户ID和角色不能为空");
            return;
        }

        if (!newRole.equals("admin") && !newRole.equals("user")) {
            JsonUtil.writeError(resp, 400, "角色只能是 admin 或 user");
            return;
        }

        // 防止管理员降级自己
        if (userId.equals(currentUser.getId()) && newRole.equals("user")) {
            JsonUtil.writeError(resp, 400, "不能降级自己的管理员权限");
            return;
        }

        if (userDao.updateRole(userId, newRole)) {
            JsonUtil.writeSuccess(resp, "角色更新成功");
        } else {
            JsonUtil.writeError(resp, 500, "角色更新失败");
        }
    }
}
