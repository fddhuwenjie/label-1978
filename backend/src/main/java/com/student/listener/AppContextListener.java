package com.student.listener;

import com.student.dao.SessionDao;
import com.student.dao.SessionDaoImpl;
import com.student.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);
    private Thread cleanupThread;
    private volatile boolean running = true;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing database connection pool...");
        try {
            DBUtil.init();
            logger.info("Database connection pool initialized.");
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }

        SessionDao sessionDao = new SessionDaoImpl();
        cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(60000);
                    sessionDao.cleanExpired();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("Session cleanup failed", e);
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.setName("session-cleanup-thread");
        cleanupThread.start();
        logger.info("Session cleanup thread started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        running = false;
        if (cleanupThread != null) {
            cleanupThread.interrupt();
        }
        logger.info("Closing database connection pool...");
        DBUtil.close();
        logger.info("Database connection pool closed.");
    }
}
