package com.hospital.management.controller.schedule;

import com.hospital.management.model.AuthenticatedUser;
import com.hospital.management.model.Page;
import com.hospital.management.service.DoctorScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorScheduleListServletDiagnosticsTest {
    @Mock DoctorScheduleService service;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher listDispatcher;
    @Mock RequestDispatcher errorDispatcher;

    DoctorScheduleListServlet servlet;

    @BeforeEach void setUp() {
        servlet = new DoctorScheduleListServlet(service);
        lenient().when(request.getRequestURI()).thenReturn("/hospital/schedules");
        lenient().when(request.getRequestDispatcher("/WEB-INF/views/schedules/list.jsp"))
                .thenReturn(listDispatcher);
        lenient().when(request.getRequestDispatcher("/WEB-INF/views/error/500.jsp"))
                .thenReturn(errorDispatcher);
    }

    @Test void missingSessionIsHandledWithoutRuntimeFailure() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoInteractions(service);
    }

    @Test void nullServiceResultsUseSafeEmptyValues() throws Exception {
        authenticatedAdmin();
        when(service.listSchedules(any(), isNull(), isNull(), isNull(), isNull())).thenReturn(null);
        when(service.getActiveDoctors()).thenReturn(null);

        servlet.doGet(request, response);

        verify(listDispatcher).forward(request, response);
        verify(request).setAttribute(eq("schedulePage"), argThat(value ->
                value instanceof Page<?> && ((Page<?>) value).getContent().isEmpty()));
        verify(request).setAttribute("doctors", List.of());
        verify(request).setAttribute("departments", List.of());
    }

    @Test void runtimeServiceFailureForwardsToExisting500Page() throws Exception {
        authenticatedAdmin();
        RuntimeException original = new RuntimeException("diagnostic test failure");
        when(service.listSchedules(any(), isNull(), isNull(), isNull(), isNull()))
                .thenThrow(original);

        servlet.doGet(request, response);

        verify(errorDispatcher).forward(request, response);
        verify(listDispatcher, never()).forward(any(), any());
        assertNotNull(original.getStackTrace());
    }

    private void authenticatedAdmin() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("authenticatedUser"))
                .thenReturn(new AuthenticatedUser(1L, "admin", "admin@example.com", "ADMIN"));
    }
}
