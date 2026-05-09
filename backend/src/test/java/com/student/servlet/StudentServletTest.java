package com.student.servlet;

import com.student.dao.StudentDao;
import com.student.model.Student;
import com.student.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudentServletTest {

    @Mock
    private StudentDao studentDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private StudentServlet studentServlet;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        studentServlet = new StudentServlet();
        
        Field studentDaoField = StudentServlet.class.getDeclaredField("studentDao");
        studentDaoField.setAccessible(true);
        studentDaoField.set(studentServlet, studentDao);
        
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testCreateStudent_Success() throws Exception {
        Student student = new Student();
        student.setStudentNo("2023001");
        student.setName("张三");
        student.setGender("男");
        student.setAge(20);
        student.setClassName("计算机1班");
        student.setMajor("计算机科学与技术");
        student.setPhone("13800138000");
        student.setAddress("北京市");

        String jsonBody = JsonUtil.toJson(student);

        when(request.getPathInfo()).thenReturn(null);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));
        when(request.getAttribute("userRole")).thenReturn("admin");
        when(studentDao.findByStudentNo("2023001")).thenReturn(null);
        when(studentDao.create(any(Student.class))).thenReturn(true);

        studentServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Create Student Success Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":200"));
        assertTrue(responseContent.contains("添加学生成功"));
    }

    @Test
    public void testListStudents_ReturnsCorrectCount() throws Exception {
        List<Student> students = new ArrayList<>();
        
        Student student1 = new Student();
        student1.setId(1);
        student1.setStudentNo("2023001");
        student1.setName("张三");
        
        Student student2 = new Student();
        student2.setId(2);
        student2.setStudentNo("2023002");
        student2.setName("李四");
        
        students.add(student1);
        students.add(student2);

        when(request.getPathInfo()).thenReturn(null);
        when(studentDao.findAll()).thenReturn(students);

        studentServlet.doGet(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("List Students Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":200"));
        assertTrue(responseContent.contains("2023001"));
        assertTrue(responseContent.contains("2023002"));
        assertTrue(responseContent.contains("张三"));
        assertTrue(responseContent.contains("李四"));
    }

    @Test
    public void testDeleteStudent_AfterDeletionListReduces() throws Exception {
        int studentId = 1;
        
        List<Student> beforeDelete = new ArrayList<>();
        Student student1 = new Student();
        student1.setId(1);
        student1.setStudentNo("2023001");
        student1.setName("张三");
        
        Student student2 = new Student();
        student2.setId(2);
        student2.setStudentNo("2023002");
        student2.setName("李四");
        
        beforeDelete.add(student1);
        beforeDelete.add(student2);

        List<Student> afterDelete = new ArrayList<>();
        afterDelete.add(student2);

        when(request.getPathInfo()).thenReturn("/" + studentId);
        when(request.getAttribute("userRole")).thenReturn("admin");
        when(studentDao.delete(studentId)).thenReturn(true);
        when(studentDao.findAll()).thenReturn(afterDelete);

        studentServlet.doDelete(request, response);

        String deleteResponse = responseWriter.toString();
        System.out.println("Delete Student Response: " + deleteResponse);
        
        assertTrue(deleteResponse.contains("\"code\":200"));
        assertTrue(deleteResponse.contains("删除学生成功"));

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getPathInfo()).thenReturn(null);
        
        studentServlet.doGet(request, response);
        
        String listResponse = responseWriter.toString();
        System.out.println("List After Delete Response: " + listResponse);
        
        assertTrue(listResponse.contains("\"code\":200"));
        assertFalse(listResponse.contains("2023001"));
        assertFalse(listResponse.contains("张三"));
        assertTrue(listResponse.contains("2023002"));
        assertTrue(listResponse.contains("李四"));
    }

    @Test
    public void testCreateStudent_StudentNoExists() throws Exception {
        Student student = new Student();
        student.setStudentNo("2023001");
        student.setName("张三");
        student.setGender("男");
        student.setAge(20);
        student.setClassName("计算机1班");
        student.setMajor("计算机科学与技术");
        student.setPhone("13800138000");
        student.setAddress("北京市");

        Student existingStudent = new Student();
        existingStudent.setId(1);
        existingStudent.setStudentNo("2023001");

        String jsonBody = JsonUtil.toJson(student);

        when(request.getPathInfo()).thenReturn(null);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));
        when(request.getAttribute("userRole")).thenReturn("admin");
        when(studentDao.findByStudentNo("2023001")).thenReturn(existingStudent);

        studentServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Create Student Exists Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":400"));
        assertTrue(responseContent.contains("学号已存在"));
    }

    @Test
    public void testCreateStudent_NonAdminUser() throws Exception {
        Student student = new Student();
        student.setStudentNo("2023001");
        student.setName("张三");

        String jsonBody = JsonUtil.toJson(student);

        when(request.getPathInfo()).thenReturn(null);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));
        when(request.getAttribute("userRole")).thenReturn("user");

        studentServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Create Student Non-Admin Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":403"));
        assertTrue(responseContent.contains("需要管理员权限"));
    }
}
