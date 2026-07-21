package com.hospital.management.controller.admin;

import com.hospital.management.service.DoctorService;
import com.hospital.management.service.ServiceResult;
import com.hospital.management.service.impl.DoctorServiceImpl;
import com.hospital.management.util.RequestUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/doctors/status")
public class DoctorStatusServlet extends HttpServlet {
    private final DoctorService service;
    public DoctorStatusServlet(){this(new DoctorServiceImpl());} public DoctorStatusServlet(DoctorService service){this.service=service;}
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException{
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,"Doctor status changes require POST");
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        Long id=Form.id(request.getParameter("doctorId"));String action=request.getParameter("action");
        if(id==null||!("activate".equals(action)||"deactivate".equals(action))){response.sendError(400);return;}
        ServiceResult<Void> result="activate".equals(action)?service.activateDoctor(id,RequestUtil.buildAuditContext(request)):service.deactivateDoctor(id,RequestUtil.buildAuditContext(request));
        String query=result.isSuccess()?"message="+("activate".equals(action)?"activated":"deactivated"):
                "error="+(result.getStatus()==ServiceResult.Status.CONFLICT?"blocked":"error");
        response.sendRedirect(request.getContextPath()+"/admin/doctors?"+query);
    }
}
