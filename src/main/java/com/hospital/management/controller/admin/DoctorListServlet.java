package com.hospital.management.controller.admin;

import com.hospital.management.model.PageRequest;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.impl.DoctorServiceImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;

@WebServlet("/admin/doctors")
public class DoctorListServlet extends HttpServlet {
    private static final Set<String> STATUSES = Set.of("ACTIVE", "INACTIVE");
    private final DoctorService service;
    public DoctorListServlet() { this(new DoctorServiceImpl()); }
    public DoctorListServlet(DoctorService service) { this.service = service; }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = integer(request.getParameter("page"), 1);
        int size = integer(request.getParameter("size"), 10);
        PageRequest pageRequest = new PageRequest(page, size, request.getParameter("search"));
        Long departmentId = Form.id(request.getParameter("departmentId"));
        String status = status(request.getParameter("status"));
        request.setAttribute("doctorPage", service.listDoctors(pageRequest, departmentId, status));
        request.setAttribute("departments", service.getAvailableDepartments(null));
        request.setAttribute("search", pageRequest.getSearchTerm());
        request.setAttribute("departmentId", departmentId);
        request.setAttribute("status", status == null ? "" : status);
        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("error", request.getParameter("error"));
        request.getRequestDispatcher("/WEB-INF/views/admin/doctors/list.jsp").forward(request, response);
    }
    private int integer(String value, int fallback) { try { return Integer.parseInt(value); } catch (Exception ignored) { return fallback; } }
    private String status(String value) { if (value == null || value.isBlank()) return null; String v=value.trim().toUpperCase(Locale.ROOT); return STATUSES.contains(v)?v:null; }
}
