<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<main class="container py-5">
    <h1>Welcome, <c:out value="${sessionScope.authenticatedUser.username}"/></h1>
    <p>Role: <c:out value="${sessionScope.authenticatedUser.roleName}"/></p>
    <div class="row g-3 mt-3">
        <c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/"><div class="card-body"><h2 class="h5">Administration</h2></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='RECEPTIONIST'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/patients"><div class="card-body"><h2 class="h5">Patient Management</h2></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='DOCTOR'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/schedules"><div class="card-body"><h2 class="h5">My Schedules</h2></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'||sessionScope.authenticatedUser.roleName=='RECEPTIONIST'||sessionScope.authenticatedUser.roleName=='DOCTOR'||sessionScope.authenticatedUser.roleName=='PATIENT'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/appointments"><div class="card-body"><h2 class="h5">Appointments</h2></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='PATIENT'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/appointments/create"><div class="card-body"><h2 class="h5">Book Appointment</h2></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'||sessionScope.authenticatedUser.roleName=='DOCTOR'||sessionScope.authenticatedUser.roleName=='PATIENT'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/medical-records"><div class="card-body"><h2 class="h5">${sessionScope.authenticatedUser.roleName=='ADMIN'?'Medical Records':'My Medical Records'}</h2></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'||sessionScope.authenticatedUser.roleName=='RECEPTIONIST'||sessionScope.authenticatedUser.roleName=='NURSE'||sessionScope.authenticatedUser.roleName=='BILLING_STAFF'||sessionScope.authenticatedUser.roleName=='DOCTOR'||sessionScope.authenticatedUser.roleName=='PATIENT'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admissions"><div class="card-body"><h2 class="h5">Admissions</h2><p class="mb-0">View admission, transfer and discharge information.</p></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'||sessionScope.authenticatedUser.roleName=='BILLING_STAFF'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/rooms"><div class="card-body"><h2 class="h5">Room Management</h2><p class="mb-0">View rooms, types and daily rates.</p></div></a></div>
        </c:if>
        <c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'||sessionScope.authenticatedUser.roleName=='NURSE'}">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/beds"><div class="card-body"><h2 class="h5">Bed Management</h2><p class="mb-0">View bed availability and status.</p></div></a></div>
        </c:if>
    </div>
</main>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
