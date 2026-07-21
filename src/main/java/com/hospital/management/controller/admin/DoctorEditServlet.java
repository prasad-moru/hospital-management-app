package com.hospital.management.controller.admin;

import com.hospital.management.model.Doctor;
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
import java.util.Optional;

@WebServlet("/admin/doctors/edit")
public class DoctorEditServlet extends HttpServlet {
    private final DoctorService service;
    public DoctorEditServlet(){this(new DoctorServiceImpl());} public DoctorEditServlet(DoctorService service){this.service=service;}
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        Long id=Form.id(request.getParameter("id")); if(id==null){response.sendError(400);return;}
        Optional<Doctor> doctor=service.getDoctor(id); if(doctor.isEmpty()){response.sendError(404);return;}
        show(request,response,Form.doctor(doctor.get()));
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8"); Long id=Form.id(request.getParameter("doctorId")); if(id==null){response.sendError(400);return;}
        DoctorForm form=Form.doctor(request,id); ServiceResult<Void> result=service.updateDoctor(form, RequestUtil.buildAuditContext(request)); Form.clearPasswords(form);
        if(result.isSuccess()){response.sendRedirect(request.getContextPath()+"/admin/doctors?message=updated");return;}
        if(result.getStatus()==ServiceResult.Status.NOT_FOUND){response.sendError(404);return;}
        request.setAttribute("errors",result.getErrors());request.setAttribute("errorMessage",result.getMessage());show(request,response,form);
    }
    private void show(HttpServletRequest request,HttpServletResponse response,DoctorForm form)throws ServletException,IOException{
        request.setAttribute("formMode","EDIT");request.setAttribute("doctorForm",form);request.setAttribute("departments",service.getAvailableDepartments(form.getDepartmentId()));
        request.getRequestDispatcher("/WEB-INF/views/admin/doctors/form.jsp").forward(request,response);
    }
}
