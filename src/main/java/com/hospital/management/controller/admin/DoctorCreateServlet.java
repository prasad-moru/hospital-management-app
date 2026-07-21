package com.hospital.management.controller.admin;

import com.hospital.management.model.DoctorForm;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.ServiceResult;
import com.hospital.management.service.impl.DoctorServiceImpl;
import com.hospital.management.util.RequestUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/doctors/create")
public class DoctorCreateServlet extends HttpServlet {
    private final DoctorService service;
    public DoctorCreateServlet() { this(new DoctorServiceImpl()); }
    public DoctorCreateServlet(DoctorService service) { this.service = service; }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DoctorForm form = new DoctorForm(); form.setStatus("ACTIVE");
        show(request, response, form, "CREATE");
    }
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        DoctorForm form = Form.doctor(request, null);
        ServiceResult<Long> result = service.createDoctor(form, RequestUtil.buildAuditContext(request));
        Form.clearPasswords(form);
        if (result.isSuccess()) { response.sendRedirect(request.getContextPath()+"/admin/doctors?message=created"); return; }
        request.setAttribute("errors", result.getErrors()); request.setAttribute("errorMessage", result.getMessage());
        show(request, response, form, "CREATE");
    }
    private void show(HttpServletRequest request, HttpServletResponse response, DoctorForm form, String mode) throws ServletException, IOException {
        request.setAttribute("formMode", mode); request.setAttribute("doctorForm", form);
        request.setAttribute("departments", service.getAvailableDepartments(form.getDepartmentId()));
        request.getRequestDispatcher("/WEB-INF/views/admin/doctors/form.jsp").forward(request, response);
    }
}
