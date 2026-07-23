<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Admit Patient</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<main class="container py-4">
    <h1>Admit Patient</h1>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
    </c:if>

    <form method="post" id="admissionForm" data-context-path="${pageContext.request.contextPath}" novalidate>
        <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">

        <div class="row g-3">
            <div class="col-md-3">
                <label class="form-label" for="patientId">Patient ID</label>
                <input id="patientId" class="form-control ${not empty errors.patientId?'is-invalid':''}" name="patientId" type="number" min="1" value="<c:out value='${admissionForm.patientId}'/>" required>
                <c:if test="${not empty errors.patientId}"><div class="invalid-feedback"><c:out value="${errors.patientId}"/></div></c:if>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="doctorId">Doctor ID</label>
                <input id="doctorId" class="form-control ${not empty errors.doctorId?'is-invalid':''}" name="doctorId" type="number" min="1" value="<c:out value='${admissionForm.doctorId}'/>" required>
                <c:if test="${not empty errors.doctorId}"><div class="invalid-feedback"><c:out value="${errors.doctorId}"/></div></c:if>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="departmentId">Department ID</label>
                <input id="departmentId" class="form-control ${not empty errors.departmentId?'is-invalid':''}" name="departmentId" type="number" min="1" value="<c:out value='${admissionForm.departmentId}'/>" required>
                <c:if test="${not empty errors.departmentId}"><div class="invalid-feedback"><c:out value="${errors.departmentId}"/></div></c:if>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="appointmentId">Appointment ID <span class="text-muted">(optional)</span></label>
                <input id="appointmentId" class="form-control ${not empty errors.appointmentId?'is-invalid':''}" name="appointmentId" type="number" min="1" value="<c:out value='${admissionForm.appointmentId}'/>">
                <c:if test="${not empty errors.appointmentId}"><div class="invalid-feedback"><c:out value="${errors.appointmentId}"/></div></c:if>
            </div>
        </div>

        <label class="form-label mt-3" for="roomType">Room type</label>
        <select id="roomType" class="form-select">
            <option value="">Any active room</option>
            <c:forEach items="${['GENERAL','SEMI_PRIVATE','PRIVATE','ICU','EMERGENCY','MATERNITY','PEDIATRIC','OTHER']}" var="type">
                <option value="${type}"><c:out value="${type}"/></option>
            </c:forEach>
        </select>

        <label class="form-label mt-3" for="bedId">Available bed</label>
        <select id="bedId" class="form-select ${not empty errors.bedId?'is-invalid':''}" name="bedId" data-selected="<c:out value='${admissionForm.bedId}'/>" required>
            <option value="">Loading available beds...</option>
        </select>
        <c:if test="${not empty errors.bedId}"><div class="invalid-feedback"><c:out value="${errors.bedId}"/></div></c:if>
        <div class="form-text">Only AVAILABLE beds in ACTIVE rooms assigned to the selected department are listed.</div>

        <label class="form-label mt-3" for="expectedDischargeDate">Expected discharge</label>
        <input id="expectedDischargeDate" class="form-control ${not empty errors.expectedDischargeDate?'is-invalid':''}" type="date" name="expectedDischargeDate" value="<c:out value='${admissionForm.expectedDischargeDate}'/>">
        <c:if test="${not empty errors.expectedDischargeDate}"><div class="invalid-feedback"><c:out value="${errors.expectedDischargeDate}"/></div></c:if>

        <label class="form-label mt-3" for="admissionReason">Admission reason</label>
        <textarea id="admissionReason" class="form-control ${not empty errors.admissionReason?'is-invalid':''}" name="admissionReason" maxlength="1000" required><c:out value="${admissionForm.admissionReason}"/></textarea>
        <c:if test="${not empty errors.admissionReason}"><div class="invalid-feedback"><c:out value="${errors.admissionReason}"/></div></c:if>

        <button class="btn btn-primary mt-3">Admit Patient</button>
    </form>
</main>
<script src="${pageContext.request.contextPath}/assets/js/admissions.js" defer></script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
