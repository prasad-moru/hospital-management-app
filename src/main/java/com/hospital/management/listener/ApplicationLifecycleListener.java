package com.hospital.management.listener;

import com.hospital.management.util.DatabaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Manages application-wide resources over the web application's lifecycle. */
@WebListener
public class ApplicationLifecycleListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLifecycleListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("Online Hospital Management System is starting");
        // The pool remains lazy so a temporarily unavailable database cannot block deployment.
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DatabaseConnectionManager.shutdown();
        LOGGER.info("Online Hospital Management System has stopped");
    }
}
