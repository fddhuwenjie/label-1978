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
    private static final long CLEANUP_INTERVAL_MS = 60 * 1000;
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

        logger.info("Starting session cleanup thread...");
        SessionDao sessionDao = new SessionDaoImpl();
        cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    logger.debug("Cleaning expired sessions...");
                    sessionDao.cleanExpired();
                    Thread.sleep(CLEANUP_INTERVAL_MS);
                } catch (InterruptedException e) {
                    logger.info("Session cleanup thread interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("Error cleaning expired sessions", e);
                    try {
                        Thread.sleep(CLEANUP_INTERVAL_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }, "SessionCleanupThread");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
        logger.info("Session cleanup thread started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Stopping session cleanup thread...");
        running = false;
        if (cleanupThread != null) {
            cleanupThread.interrupt();
            try {
                cleanupThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Session cleanup thread stopped.");

        logger.info("Closing database connection pool...");
        DBUtil.close();
        logger.info("Database connection pool closed.");
    }
}
