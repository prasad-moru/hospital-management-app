package com.hospital.management.controller;

import com.hospital.management.util.DatabaseConnectionManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseHealthServletTest {

    @Test
    void returnsUpWhenOracleQuerySucceeds() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        ResultSet resultSet = mock(ResultSet.class);
        StringWriter body = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(body));
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT 1 FROM DUAL")).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        try (MockedStatic<DatabaseConnectionManager> manager = mockStatic(DatabaseConnectionManager.class)) {
            manager.when(DatabaseConnectionManager::getConnection).thenReturn(connection);
            new DatabaseHealthServlet().doGet(request, response);
        }

        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals("{\"status\":\"UP\",\"database\":\"Oracle\"}", body.toString());
    }

    @Test
    void returnsDownWithoutLeakingDetailsWhenConnectionFails() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        try (MockedStatic<DatabaseConnectionManager> manager = mockStatic(DatabaseConnectionManager.class)) {
            manager.when(DatabaseConnectionManager::getConnection)
                    .thenThrow(new SQLException("sensitive internal connection detail"));
            new DatabaseHealthServlet().doGet(request, response);
        }

        verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        assertEquals("{\"status\":\"DOWN\",\"database\":\"Oracle\"}", body.toString());
    }
}
