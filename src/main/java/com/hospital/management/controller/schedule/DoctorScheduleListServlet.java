package com.hospital.management.controller.schedule;

import com.hospital.management.model.AuthenticatedUser;
import com.hospital.management.model.Department;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.DoctorSchedule;
import com.hospital.management.model.Page;
import com.hospital.management.model.PageRequest;
import com.hospital.management.service.DoctorScheduleService;
import com.hospital.management.service.impl.DoctorScheduleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/schedules")
public class DoctorScheduleListServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorScheduleListServlet.class);
    private static final String VIEW = "/WEB-INF/views/schedules/list.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error/500.jsp";
    private final DoctorScheduleService service;

    public DoctorScheduleListServlet() { this(new DoctorScheduleServiceImpl()); }
    public DoctorScheduleListServlet(DoctorScheduleService service) { this.service = service; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            AuthenticatedUser user = ScheduleRequest.user(request);
            LOGGER.info("Rendering doctor schedule list uri={} username={} role={}",
                    request.getRequestURI(), safeUsername(user), safeRole(user));
            if (user == null) {
                LOGGER.error("Doctor schedule list request has no valid authenticated user");
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            int pageNumber = number(request.getParameter("page"), 1);
            int pageSize = number(request.getParameter("size"), 10);
            Long doctorId = ScheduleRequest.id(request.getParameter("doctorId"));
            Long departmentId = ScheduleRequest.id(request.getParameter("departmentId"));
            String dayOfWeek = day(request.getParameter("dayOfWeek"));
            Boolean available = bool(request.getParameter("available"));

            LOGGER.info("Schedule filters page={} size={} doctorId={} departmentId={} dayOfWeek={} available={}",
                    pageNumber, pageSize, doctorId, departmentId, dayOfWeek, available);

            if ("DOCTOR".equals(user.getRoleName())) {
                LOGGER.info("About to execute DoctorScheduleService.getOwnDoctor");
                Optional<Doctor> ownDoctor = nullSafeOptional(service.getOwnDoctor(user));
                if (ownDoctor.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                doctorId = ownDoctor.get().getDoctorId();
                request.setAttribute("ownDoctor", ownDoctor.get());
            }

            PageRequest pageRequest = new PageRequest(
                    pageNumber, pageSize, request.getParameter("search"));
            LOGGER.info("About to execute DoctorScheduleService.listSchedules");
            Page<DoctorSchedule> schedulePage = service.listSchedules(
                    pageRequest, doctorId, departmentId, dayOfWeek, available);
            if (schedulePage == null) {
                LOGGER.error("DoctorScheduleService.listSchedules returned null; using an empty page");
                schedulePage = new Page<>(List.of(), pageRequest.getPageNumber(),
                        pageRequest.getPageSize(), 0);
            }

            LOGGER.info("About to execute DoctorScheduleService.getActiveDoctors");
            List<Doctor> doctors = service.getActiveDoctors();
            if (doctors == null) {
                LOGGER.error("DoctorScheduleService.getActiveDoctors returned null; using an empty list");
                doctors = List.of();
            }
            List<Department> departments = departmentsFrom(doctors);
            if (departments == null) departments = List.of();

            request.setAttribute("schedulePage", schedulePage);
            request.setAttribute("doctors", doctors);
            request.setAttribute("departments", departments);
            request.setAttribute("doctorId", doctorId);
            request.setAttribute("departmentId", departmentId);
            request.setAttribute("dayOfWeek", dayOfWeek);
            request.setAttribute("available", available);
            request.setAttribute("search", pageRequest.getSearchTerm());
            request.setAttribute("message", request.getParameter("message"));
            request.setAttribute("error", request.getParameter("error"));
            LOGGER.info("About to forward doctor schedule list to {}", VIEW);
            request.getRequestDispatcher(VIEW).forward(request, response);
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to render doctor schedule list", exception);
            request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
        }
    }

    private List<Department> departmentsFrom(List<Doctor> doctors) {
        if (doctors == null) return List.of();
        Map<Long, Department> unique = new LinkedHashMap<>();
        for (Doctor doctor : doctors) {
            if (doctor == null || doctor.getDepartmentId() == null) continue;
            unique.putIfAbsent(doctor.getDepartmentId(),
                    new Department(doctor.getDepartmentId(), doctor.getDepartmentName(), null, null, "ACTIVE"));
        }
        return new ArrayList<>(unique.values());
    }

    private Optional<Doctor> nullSafeOptional(Optional<Doctor> value) {
        return value == null ? Optional.empty() : value;
    }
    private String safeUsername(AuthenticatedUser user) { return user == null ? "unavailable" : user.getUsername(); }
    private String safeRole(AuthenticatedUser user) { return user == null ? "unavailable" : user.getRoleName(); }
    private int number(String value, int fallback) { try { return Integer.parseInt(value); } catch (Exception ignored) { return fallback; } }
    private String day(String value) { try { return java.time.DayOfWeek.valueOf(value).name(); } catch (Exception ignored) { return null; } }
    private Boolean bool(String value) { return "true".equals(value) ? Boolean.TRUE : "false".equals(value) ? Boolean.FALSE : null; }
}
