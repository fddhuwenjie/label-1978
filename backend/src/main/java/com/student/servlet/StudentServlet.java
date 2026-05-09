package com.student.servlet;

import com.student.dao.StudentDao;
import com.student.dao.StudentDaoImpl;
import com.student.exception.DaoException;
import com.student.exception.ValidationException;
import com.student.model.Student;
import com.student.util.JsonUtil;
import com.student.util.ValidationUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class StudentServlet extends HttpServlet {
    private StudentDao studentDao;

    @Override
    public void init() throws ServletException {
        studentDao = new StudentDaoImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        try {
            if (path == null || path.equals("/") || path.equals("/list")) {
                handleList(req, resp);
            } else if (path.equals("/search")) {
                handleSearch(req, resp);
            } else {
                int id = Integer.parseInt(path.substring(1));
                handleGetById(id, resp);
            }
        } catch (NumberFormatException e) {
            JsonUtil.writeError(resp, 400, "无效的学生ID");
        } catch (DaoException e) {
            JsonUtil.writeError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            JsonUtil.writeError(resp, 403, "需要管理员权限");
            return;
        }
        try {
            handleCreate(req, resp);
        } catch (ValidationException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (DaoException e) {
            JsonUtil.writeError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            JsonUtil.writeError(resp, 403, "需要管理员权限");
            return;
        }
        try {
            handleUpdate(req, resp);
        } catch (ValidationException e) {
            JsonUtil.writeError(resp, 400, e.getMessage());
        } catch (DaoException e) {
            JsonUtil.writeError(resp, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) {
            JsonUtil.writeError(resp, 403, "需要管理员权限");
            return;
        }
        String path = req.getPathInfo();
        if (path != null && path.length() > 1) {
            try {
                int id = Integer.parseInt(path.substring(1));
                handleDelete(id, resp);
            } catch (NumberFormatException e) {
                JsonUtil.writeError(resp, 400, "无效的学生ID");
            } catch (DaoException e) {
                JsonUtil.writeError(resp, 500, e.getMessage());
            }
        } else {
            JsonUtil.writeError(resp, 400, "学生ID不能为空");
        }
    }

    private boolean isAdmin(HttpServletRequest req) {
        String role = (String) req.getAttribute("userRole");
        return "admin".equals(role);
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Student> students = studentDao.findAll();
        JsonUtil.writeSuccess(resp, students);
    }

    private void handleSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        if (keyword == null || keyword.isEmpty()) {
            handleList(req, resp);
            return;
        }
        List<Student> students = studentDao.search(keyword);
        JsonUtil.writeSuccess(resp, students);
    }

    private void handleGetById(int id, HttpServletResponse resp) throws IOException {
        Student student = studentDao.findById(id);
        if (student != null) {
            JsonUtil.writeSuccess(resp, student);
        } else {
            JsonUtil.writeError(resp, 404, "学生不存在");
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student student = JsonUtil.fromJson(req, Student.class);
        ValidationUtil.validateStudent(student);
        if (studentDao.findByStudentNo(student.getStudentNo()) != null) {
            JsonUtil.writeError(resp, 400, "学号已存在");
            return;
        }
        if (studentDao.create(student)) {
            JsonUtil.writeSuccess(resp, "添加学生成功");
        } else {
            JsonUtil.writeError(resp, 500, "添加学生失败");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student student = JsonUtil.fromJson(req, Student.class);
        if (student.getId() == null) {
            JsonUtil.writeError(resp, 400, "学生ID不能为空");
            return;
        }
        ValidationUtil.validateStudent(student);
        if (studentDao.update(student)) {
            JsonUtil.writeSuccess(resp, "更新学生成功");
        } else {
            JsonUtil.writeError(resp, 500, "更新学生失败");
        }
    }

    private void handleDelete(int id, HttpServletResponse resp) throws IOException {
        if (studentDao.delete(id)) {
            JsonUtil.writeSuccess(resp, "删除学生成功");
        } else {
            JsonUtil.writeError(resp, 500, "删除学生失败");
        }
    }
}
