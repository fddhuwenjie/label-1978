package com.student.filter;

import com.student.dao.SessionDao;
import com.student.dao.UserDao;
import com.student.model.Session;
import com.student.model.User;
import com.student.util.DBUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthFilterTest {

    @Mock
    private SessionDao sessionDao;

    @Mock
    private UserDao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private AuthFilter authFilter;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        authFilter = new AuthFilter();
        
        Field sessionDaoField = AuthFilter.class.getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(authFilter, sessionDao);
        
        Field userDaoField = AuthFilter.class.getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(authFilter, userDao);
        
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testNoSession_RequestIntercepted_Returns401() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/students");
        when(request.getHeader("X-Session-Id")).thenReturn(null);

        authFilter.doFilter(request, response, filterChain);

        String responseContent = responseWriter.toString();
        System.out.println("No Session Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":401"));
        assertTrue(responseContent.contains("未登录，请先登录"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void testEmptySession_RequestIntercepted_Returns401() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/students");
        when(request.getHeader("X-Session-Id")).thenReturn("");

        authFilter.doFilter(request, response, filterChain);

        String responseContent = responseWriter.toString();
        System.out.println("Empty Session Response: " + responseContent);
        
        assertTrue(responseContent.contains("\"code\":401"));
        assertTrue(responseContent.contains("未登录，请先登录"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void testPublicPath_Login_RequestPassesThrough() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testPublicPath_Register_RequestPassesThrough() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testPublicPath_Logout_RequestPassesThrough() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/auth/logout");

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testValidSession_RequestPassesThrough() throws Exception {
        String sessionId = "valid-session-id";
        int userId = 1;
        
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setExpiresAt(new Timestamp(System.currentTimeMillis() + 30000));

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setRole("user");

        when(request.getRequestURI()).thenReturn("/api/students");
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection mockConnection = mock(Connection.class);
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(sessionDao.findBySessionId(eq(mockConnection), eq(sessionId))).thenReturn(session);
            when(sessionDao.refresh(eq(mockConnection), eq(sessionId), any(Timestamp.class))).thenReturn(true);
            when(userDao.findById(eq(mockConnection), eq(userId))).thenReturn(user);

            authFilter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
            verify(request).setAttribute("currentUser", user);
            verify(request).setAttribute("userId", userId);
            verify(request).setAttribute("userRole", "user");
        }
    }

    @Test
    public void testExpiredSession_RequestIntercepted_Returns401() throws Exception {
        String sessionId = "expired-session-id";

        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUserId(1);
        session.setExpiresAt(new Timestamp(System.currentTimeMillis() - 30000));

        when(request.getRequestURI()).thenReturn("/api/students");
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection mockConnection = mock(Connection.class);
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);

            when(sessionDao.findBySessionId(eq(mockConnection), eq(sessionId))).thenReturn(session);
            when(sessionDao.delete(eq(mockConnection), eq(sessionId))).thenReturn(true);

            authFilter.doFilter(request, response, filterChain);

            String responseContent = responseWriter.toString();
            System.out.println("Expired Session Response: " + responseContent);

            assertTrue(responseContent.contains("\"code\":401"));
            assertTrue(responseContent.contains("登录已过期，请重新登录"));
            verify(sessionDao).delete(eq(mockConnection), eq(sessionId));
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    @Test
    public void testValidSession_AdminRole_SetCorrectly() throws Exception {
        String sessionId = "admin-session-id";
        int userId = 1;
        
        Session session = new Session();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setExpiresAt(new Timestamp(System.currentTimeMillis() + 30000));

        User user = new User();
        user.setId(userId);
        user.setUsername("admin");
        user.setRole("admin");

        when(request.getRequestURI()).thenReturn("/api/students");
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        try (MockedStatic<DBUtil> mockedDBUtil = mockStatic(DBUtil.class)) {
            Connection mockConnection = mock(Connection.class);
            mockedDBUtil.when(DBUtil::getConnection).thenReturn(mockConnection);
            
            when(sessionDao.findBySessionId(eq(mockConnection), eq(sessionId))).thenReturn(session);
            when(sessionDao.refresh(eq(mockConnection), eq(sessionId), any(Timestamp.class))).thenReturn(true);
            when(userDao.findById(eq(mockConnection), eq(userId))).thenReturn(user);

            authFilter.doFilter(request, response, filterChain);

            verify(filterChain, times(1)).doFilter(request, response);
            verify(request).setAttribute("userRole", "admin");
        }
    }
}
