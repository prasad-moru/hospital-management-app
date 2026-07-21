package com.hospital.management.controller.admin;

import com.hospital.management.model.Department;
import com.hospital.management.model.Doctor;
import com.hospital.management.model.DoctorForm;
import com.hospital.management.model.Patient;
import com.hospital.management.model.PatientForm;
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

    static PatientForm patient(HttpServletRequest q, Long id) { PatientForm f=new PatientForm();f.setPatientId(id);f.setPatientNumber(q.getParameter("patientNumber"));f.setFirstName(q.getParameter("firstName"));f.setLastName(q.getParameter("lastName"));f.setDateOfBirth(q.getParameter("dateOfBirth"));f.setGender(q.getParameter("gender"));f.setBloodGroup(q.getParameter("bloodGroup"));f.setPhone(q.getParameter("phone"));f.setEmail(q.getParameter("email"));f.setAddress(q.getParameter("address"));f.setEmergencyContactName(q.getParameter("emergencyContactName"));f.setEmergencyContactPhone(q.getParameter("emergencyContactPhone"));f.setStatus(q.getParameter("status"));f.setCreateLoginAccount("true".equals(q.getParameter("createLoginAccount")));f.setUsername(q.getParameter("username"));f.setPassword(q.getParameter("password"));f.setConfirmPassword(q.getParameter("confirmPassword"));return f; }
    static PatientForm patient(Patient p){PatientForm f=new PatientForm();f.setPatientId(p.getPatientId());f.setPatientNumber(p.getPatientNumber());f.setFirstName(p.getFirstName());f.setLastName(p.getLastName());f.setDateOfBirth(p.getDateOfBirth()==null?"":p.getDateOfBirth().toString());f.setGender(p.getGender());f.setBloodGroup(p.getBloodGroup());f.setPhone(p.getPhone());f.setEmail(p.getEmail());f.setAddress(p.getAddress());f.setEmergencyContactName(p.getEmergencyContactName());f.setEmergencyContactPhone(p.getEmergencyContactPhone());f.setStatus(p.getStatus());f.setCreateLoginAccount(p.getUserId()!=null);f.setUsername(p.getUsername());return f;}
    static void clearPasswords(PatientForm f){f.setPassword(null);f.setConfirmPassword(null);}
}
