<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html><html><head><meta charset="UTF-8"><title>Printable Prescription</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/prescription-print.css"></head><body>
<div class="header"><h1>Online Hospital Management System</h1><h2>Prescription</h2></div>
<div class="meta"><div><strong>Patient:</strong> <c:out value="${prescription.patientName}"/></div><div><strong>Patient number:</strong> <c:out value="${prescription.patientNumber}"/></div><div><strong>Doctor:</strong> <c:out value="${prescription.doctorName}"/></div><div><strong>Department:</strong> <c:out value="${prescription.departmentName}"/></div><div><strong>Prescription date:</strong> <c:out value="${prescription.prescriptionDate}"/></div><div><strong>Valid until:</strong> <c:out value="${prescription.validUntil}"/></div></div>
<table><thead><tr><th>Medicine</th><th>Dosage</th><th>Frequency</th><th>Duration</th><th>Route</th><th>Instructions</th></tr></thead><tbody><c:forEach items="${prescription.items}" var="i"><tr><td><c:out value="${i.medicineName}"/></td><td><c:out value="${i.dosage}"/></td><td><c:out value="${i.frequency}"/></td><td><c:out value="${i.duration}"/></td><td><c:out value="${i.route}"/></td><td><c:out value="${i.instructions}"/></td></tr></c:forEach></tbody></table>
<p><strong>Notes:</strong> <c:out value="${prescription.notes}"/></p><div class="signature">Doctor signature: ____________________</div><button id="print-prescription" class="no-print" type="button">Print</button>
<script src="${pageContext.request.contextPath}/assets/js/prescription-print.js" defer></script></body></html>
