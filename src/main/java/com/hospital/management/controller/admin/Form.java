package com.hospital.management.controller.admin;

import com.hospital.management.model.Department;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.DoctorForm;
import javax.servlet.http.HttpServletRequest;

final class Form {
    private Form() { }

    static Long id(String value) {
        try { long parsed = Long.parseLong(value); return parsed > 0 ? parsed : null; }
        catch (Exception ignored) { return null; }
    }

    static Department department(HttpServletRequest request, Long id) {
        return new Department(id, request.getParameter("departmentName"), request.getParameter("description"),
                request.getParameter("location"), request.getParameter("status"));
    }

    static DoctorForm doctor(HttpServletRequest request, Long doctorId) {
        DoctorForm form = new DoctorForm();
        form.setDoctorId(doctorId);
        form.setUsername(request.getParameter("username"));
        form.setEmail(request.getParameter("email"));
        form.setPassword(request.getParameter("password"));
        form.setConfirmPassword(request.getParameter("confirmPassword"));
        form.setDepartmentId(id(request.getParameter("departmentId")));
        form.setRegistrationNumber(request.getParameter("registrationNumber"));
        form.setFirstName(request.getParameter("firstName"));
        form.setLastName(request.getParameter("lastName"));
        form.setSpecialization(request.getParameter("specialization"));
        form.setQualification(request.getParameter("qualification"));
        form.setPhone(request.getParameter("phone"));
        form.setConsultationFee(request.getParameter("consultationFee"));
        form.setStatus(request.getParameter("status"));
        return form;
    }

    static DoctorForm doctor(Doctor doctor) {
        DoctorForm form = new DoctorForm();
        form.setDoctorId(doctor.getDoctorId()); form.setUsername(doctor.getUsername());
        form.setEmail(doctor.getEmail()); form.setDepartmentId(doctor.getDepartmentId());
        form.setRegistrationNumber(doctor.getRegistrationNumber()); form.setFirstName(doctor.getFirstName());
        form.setLastName(doctor.getLastName()); form.setSpecialization(doctor.getSpecialization());
        form.setQualification(doctor.getQualification()); form.setPhone(doctor.getPhone());
        form.setConsultationFee(doctor.getConsultationFee() == null ? "" : doctor.getConsultationFee().toPlainString());
        form.setStatus(doctor.getStatus());
        return form;
    }

    static void clearPasswords(DoctorForm form) {
        form.setPassword(null);
        form.setConfirmPassword(null);
    }
}
