package com.student.servlet;

import com.student.dao.SessionDao;
import com.student.dao.UserDao;
import com.student.model.User;
import com.student.util.JsonUtil;
import com.student.util.PasswordUtil;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServletTest {

    @Mock
    private UserDao userDao;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AuthServlet authServlet;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        authServlet = new AuthServlet();
        
        Field userDaoField = AuthServlet.class.getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(authServlet, userDao);
        
        Field sessionDaoField = AuthServlet.class.getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(authServlet, sessionDao);
        
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testLogin_Success() throws Exception {
        String username = "testuser";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole("user");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        String jsonBody = JsonUtil.toJson(requestBody);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));
        when(userDao.findByUsername(username)).thenReturn(user);
        when(sessionDao.create(any())).thenReturn(true);

        authServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Login Success Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":200"));
        assertTrue(responseContent.contains("\"sessionId\""));
        assertTrue(responseContent.contains("\"userId\":1"));
        assertTrue(responseContent.contains("\"username\":\"testuser\""));
    }

    @Test
    public void testLogin_WrongPassword() throws Exception {
        String username = "testuser";
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = PasswordUtil.hashPassword(correctPassword);
        
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole("user");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", wrongPassword);
        String jsonBody = JsonUtil.toJson(requestBody);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));
        when(userDao.findByUsername(username)).thenReturn(user);

        authServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Login Wrong Password Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":401"));
        assertTrue(responseContent.contains("用户名或密码错误"));
    }

    @Test
    public void testLogin_EmptyUsername() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("password", "password123");
        String jsonBody = JsonUtil.toJson(requestBody);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));

        authServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Login Empty Username Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":400"));
        assertTrue(responseContent.contains("用户名和密码不能为空"));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        String username = "nonexistent";
        String password = "password123";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        String jsonBody = JsonUtil.toJson(requestBody);

        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonBody)));
        when(userDao.findByUsername(username)).thenReturn(null);

        authServlet.doPost(request, response);

        String responseContent = responseWriter.toString();
        System.out.println("Login User Not Found Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":401"));
        assertTrue(responseContent.contains("用户名或密码错误"));
    }
}
