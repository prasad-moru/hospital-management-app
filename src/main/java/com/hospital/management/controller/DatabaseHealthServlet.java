package com.hospital.management.controller;

import com.hospital.management.util.DatabaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/** Provides a minimal, non-sensitive Oracle connectivity health check. */
@WebServlet("/health/database")
public class DatabaseHealthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthServlet.class);
    private static final String HEALTH_QUERY = "SELECT 1 FROM DUAL";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(HEALTH_QUERY)) {
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"UP\",\"database\":\"Oracle\"}");
                return;
            }
            LOGGER.warn("Oracle health query returned an unexpected result");
        } catch (Exception exception) {
            LOGGER.error("Oracle database health check failed", exception);
        }

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.getWriter().write("{\"status\":\"DOWN\",\"database\":\"Oracle\"}");
    }
}
