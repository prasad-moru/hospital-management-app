<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html><html><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Reports and Analytics</title><link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body><%@ include file="/WEB-INF/views/common/header.jsp" %><main class="container-fluid px-lg-5 py-4">
<div class="d-flex justify-content-between align-items-center"><div><h1>Reports and Analytics</h1><a href="${pageContext.request.contextPath}/admin/">Admin Dashboard</a></div></div>
<%@ include file="filter.jspf" %>
<div class="row g-3">
<c:set var="metrics" value="${[['Departments',overview.totalDepartments],['Active Doctors',overview.activeDoctors],['Active Patients',overview.activePatients],['Appointments',overview.totalAppointments],['Active Admissions',overview.activeAdmissions],['Available Beds',overview.availableBeds],['Occupied Beds',overview.occupiedBeds]]}"/>
<c:forEach items="${metrics}" var="m"><div class="col-6 col-lg"><div class="card h-100"><div class="card-body"><div class="text-muted"><c:out value="${m[0]}"/></div><div class="fs-3 fw-bold"><c:out value="${m[1]}"/></div></div></div></div></c:forEach>
</div>
<div class="row g-3 mt-1">
<div class="col-md-4"><div class="card"><div class="card-body"><div class="text-muted">Total Billed</div><div class="fs-4">₹<fmt:formatNumber value="${overview.totalBilledAmount}" minFractionDigits="2" maxFractionDigits="2"/></div></div></div></div>
<div class="col-md-4"><div class="card"><div class="card-body"><div class="text-muted">Total Paid</div><div class="fs-4">₹<fmt:formatNumber value="${overview.totalPaidAmount}" minFractionDigits="2" maxFractionDigits="2"/></div></div></div></div>
<div class="col-md-4"><div class="card"><div class="card-body"><div class="text-muted">Outstanding</div><div class="fs-4">₹<fmt:formatNumber value="${overview.outstandingAmount}" minFractionDigits="2" maxFractionDigits="2"/></div></div></div></div>
</div>
<h2 class="h4 mt-4">Detailed reports</h2><div class="row g-3">
<c:forEach items="${[['Appointment Report','appointments'],['Patient Report','patients'],['Doctor and Department Report','doctors'],['Admission and Bed Occupancy Report','admissions'],['Billing and Revenue Report','billing']]}" var="report">
<div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/reports/${report[1]}?dateFrom=${filter.dateFrom}&amp;dateTo=${filter.dateTo}"><div class="card-body"><h3 class="h5"><c:out value="${report[0]}"/></h3><p class="mb-0">Open filtered aggregate report, CSV export and print view.</p></div></a></div>
</c:forEach></div>
</main><%@ include file="/WEB-INF/views/common/footer.jsp" %></body></html>
