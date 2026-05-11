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
    private static final long CLEANUP_INTERVAL_MS = 60000;
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

        startSessionCleanupThread();
    }

    private void startSessionCleanupThread() {
        SessionDao sessionDao = new SessionDaoImpl();
        cleanupThread = new Thread(() -> {
            logger.info("Session cleanup thread started.");
            while (running) {
                try {
                    Thread.sleep(CLEANUP_INTERVAL_MS);
                    if (running) {
                        logger.debug("Cleaning up expired sessions...");
                        sessionDao.cleanExpired();
                        logger.debug("Expired sessions cleaned up.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Session cleanup thread interrupted.");
                    break;
                } catch (Exception e) {
                    logger.error("Error cleaning up expired sessions", e);
                }
            }
            logger.info("Session cleanup thread stopped.");
        }, "session-cleanup-thread");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
        logger.info("Session cleanup thread scheduled to run every 60 seconds.");
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

        logger.info("Closing database connection pool...");
        DBUtil.close();
        logger.info("Database connection pool closed.");
    }
}
