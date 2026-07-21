package com.hospital.management.controller.admin;

import com.hospital.management.model.Doctor;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.impl.DoctorServiceImpl;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/admin/doctors/view")
public class DoctorViewServlet extends HttpServlet {
    private final DoctorService service;
    public DoctorViewServlet() { this(new DoctorServiceImpl()); }
    public DoctorViewServlet(DoctorService service) { this.service=service; }
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        Long id=Form.id(request.getParameter("id")); if(id==null){response.sendError(400);return;}
        Optional<Doctor> doctor=service.getDoctor(id); if(doctor.isEmpty()){response.sendError(404);return;}
        request.setAttribute("doctor",doctor.get());
        request.getRequestDispatcher("/WEB-INF/views/admin/doctors/view.jsp").forward(request,response);
    }
}
