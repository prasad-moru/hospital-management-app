package com.hospital.management.controller.admin;

import com.hospital.management.model.*;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServletTest {
    @Mock DoctorService service; @Mock HttpServletRequest request; @Mock HttpServletResponse response;
    @Mock HttpSession session; @Mock RequestDispatcher dispatcher;

    @BeforeEach void setup() {
        lenient().when(request.getContextPath()).thenReturn("/hospital");
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(session.getAttribute("authenticatedUser")).thenReturn(new AuthenticatedUser(1L,"admin","admin@example.com","ADMIN"));
        lenient().when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        lenient().when(service.getAvailableDepartments(any())).thenReturn(List.of());
    }

    @Test void listUsesDefaultsAndForwards() throws Exception {
        when(service.listDoctors(any(),isNull(),isNull())).thenReturn(new Page<>(List.of(),1,10,0));
        new DoctorListServlet(service).doGet(request,response);
        ArgumentCaptor<PageRequest> page=ArgumentCaptor.forClass(PageRequest.class);verify(service).listDoctors(page.capture(),isNull(),isNull());assertEquals(1,page.getValue().getPageNumber());assertEquals(10,page.getValue().getPageSize());
        verify(dispatcher).forward(request,response);
    }

    @Test void listNormalizesInvalidParametersAndAcceptsFilters() throws Exception {
        lenient().when(request.getParameter("page")).thenReturn("bad");lenient().when(request.getParameter("size")).thenReturn("9999");lenient().when(request.getParameter("departmentId")).thenReturn("4");lenient().when(request.getParameter("status")).thenReturn("active");
        when(service.listDoctors(any(),eq(4L),eq("ACTIVE"))).thenReturn(new Page<>(List.of(),1,100,0));new DoctorListServlet(service).doGet(request,response);
        verify(service).listDoctors(any(),eq(4L),eq("ACTIVE"));
    }

    @Test void createGetShowsFormAndPostRedirects() throws Exception {
        DoctorCreateServlet servlet=new DoctorCreateServlet(service);servlet.doGet(request,response);verify(request).setAttribute("formMode","CREATE");verify(dispatcher).forward(request,response);
        stubValidForm();when(service.createDoctor(any(),any())).thenReturn(ServiceResult.success("ok",5L));servlet.doPost(request,response);verify(response).sendRedirect("/hospital/admin/doctors?message=created");
    }

    @Test void createFailureClearsPasswordsBeforeForward() throws Exception {
        stubValidForm();when(service.createDoctor(any(),any())).thenReturn(ServiceResult.failure(ServiceResult.Status.DUPLICATE,"Duplicate",Map.of("username","Exists")));
        new DoctorCreateServlet(service).doPost(request,response);
        ArgumentCaptor<Object> value=ArgumentCaptor.forClass(Object.class);verify(request,atLeastOnce()).setAttribute(eq("doctorForm"),value.capture());DoctorForm form=(DoctorForm)value.getValue();assertNull(form.getPassword());assertNull(form.getConfirmPassword());verify(dispatcher).forward(request,response);
    }

    @Test void viewHandlesValidInvalidAndMissingIds() throws Exception {
        Doctor doctor=doctor();when(request.getParameter("id")).thenReturn("5");when(service.getDoctor(5L)).thenReturn(Optional.of(doctor));new DoctorViewServlet(service).doGet(request,response);verify(request).setAttribute("doctor",doctor);verify(dispatcher).forward(request,response);
        reset(response);when(request.getParameter("id")).thenReturn("x");new DoctorViewServlet(service).doGet(request,response);verify(response).sendError(400);
        reset(response);when(request.getParameter("id")).thenReturn("6");when(service.getDoctor(6L)).thenReturn(Optional.empty());new DoctorViewServlet(service).doGet(request,response);verify(response).sendError(404);
    }

    @Test void editGetLoadsSafeFormAndMissingReturns404() throws Exception {
        when(request.getParameter("id")).thenReturn("5");when(service.getDoctor(5L)).thenReturn(Optional.of(doctor()));new DoctorEditServlet(service).doGet(request,response);
        ArgumentCaptor<Object> value=ArgumentCaptor.forClass(Object.class);verify(request).setAttribute(eq("doctorForm"),value.capture());assertNull(((DoctorForm)value.getValue()).getPassword());
        reset(response);when(request.getParameter("id")).thenReturn("6");when(service.getDoctor(6L)).thenReturn(Optional.empty());new DoctorEditServlet(service).doGet(request,response);verify(response).sendError(404);
    }

    @Test void editPostRedirectsOrClearsPasswordsOnValidation() throws Exception {
        stubValidForm();when(request.getParameter("doctorId")).thenReturn("5");when(service.updateDoctor(any(),any())).thenReturn(ServiceResult.success("ok",null));new DoctorEditServlet(service).doPost(request,response);verify(response).sendRedirect("/hospital/admin/doctors?message=updated");
        reset(response);when(service.updateDoctor(any(),any())).thenReturn(ServiceResult.failure(ServiceResult.Status.VALIDATION_ERROR,"Fix",Map.of("email","Invalid")));new DoctorEditServlet(service).doPost(request,response);
        ArgumentCaptor<Object> value=ArgumentCaptor.forClass(Object.class);verify(request,atLeastOnce()).setAttribute(eq("doctorForm"),value.capture());assertNull(((DoctorForm)value.getValue()).getPassword());
    }

    @Test void statusActivatesDeactivatesAndMapsBlockedResult() throws Exception {
        when(request.getParameter("doctorId")).thenReturn("5");when(request.getParameter("action")).thenReturn("activate");when(service.activateDoctor(eq(5L),any())).thenReturn(ServiceResult.success("ok",null));new DoctorStatusServlet(service).doPost(request,response);verify(response).sendRedirect("/hospital/admin/doctors?message=activated");
        reset(response);when(request.getParameter("action")).thenReturn("deactivate");when(service.deactivateDoctor(eq(5L),any())).thenReturn(ServiceResult.failure(ServiceResult.Status.CONFLICT,"blocked",Map.of()));new DoctorStatusServlet(service).doPost(request,response);verify(response).sendRedirect("/hospital/admin/doctors?error=blocked");
    }

    @Test void statusRejectsInvalidActionAndGetIsNotAccepted() throws Exception {
        when(request.getParameter("doctorId")).thenReturn("5");when(request.getParameter("action")).thenReturn("delete");DoctorStatusServlet servlet=new DoctorStatusServlet(service);servlet.doPost(request,response);verify(response).sendError(400);verifyNoInteractions(service);
        reset(response);servlet.doGet(request,response);verify(response).sendError(eq(HttpServletResponse.SC_METHOD_NOT_ALLOWED),anyString());
    }

    private void stubValidForm(){when(request.getParameter("username")).thenReturn("demo_user");when(request.getParameter("email")).thenReturn("demo@example.com");when(request.getParameter("password")).thenReturn("Strong@123");when(request.getParameter("confirmPassword")).thenReturn("Strong@123");when(request.getParameter("departmentId")).thenReturn("2");when(request.getParameter("registrationNumber")).thenReturn("REG-1");when(request.getParameter("firstName")).thenReturn("Demo");when(request.getParameter("lastName")).thenReturn("Doctor");when(request.getParameter("specialization")).thenReturn("General");when(request.getParameter("qualification")).thenReturn("MBBS");when(request.getParameter("phone")).thenReturn("9000000001");when(request.getParameter("consultationFee")).thenReturn("500");when(request.getParameter("status")).thenReturn("ACTIVE");}
    private Doctor doctor(){Doctor d=new Doctor();d.setDoctorId(5L);d.setDepartmentId(2L);d.setUsername("demo");d.setEmail("demo@example.com");d.setFirstName("Demo");d.setLastName("Doctor");d.setConsultationFee(java.math.BigDecimal.TEN);d.setStatus("ACTIVE");return d;}
}
