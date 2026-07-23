<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<main class="container py-5">
    <h1>Admin Dashboard</h1>
    <a href="${pageContext.request.contextPath}/dashboard">Back to main dashboard</a>
    <div class="row g-3 mt-3">
        <c:forEach items="${['Departments','Doctors','Patients']}" var="item">
            <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/${item.toLowerCase()}"><div class="card-body"><h2 class="h5"><c:out value="${item}"/></h2><p>Manage hospital <c:out value="${item.toLowerCase()}"/>.</p></div></a></div>
        </c:forEach>
        <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/schedules"><div class="card-body"><h2 class="h5">Doctor Schedules</h2><p>Manage doctor availability windows.</p></div></a></div>
        <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admissions"><div class="card-body"><h2 class="h5">Admissions</h2><p>Manage admissions, transfers and discharges.</p></div></a></div>
        <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/rooms"><div class="card-body"><h2 class="h5">Rooms</h2><p>Manage hospital rooms and daily rates.</p></div></a></div>
        <div class="col-md-4"><a class="card h-100 text-decoration-none" href="${pageContext.request.contextPath}/admin/beds"><div class="card-body"><h2 class="h5">Beds</h2><p>Manage room beds and availability.</p></div></a></div>
        <c:forEach items="${['Users','Reports']}" var="item">
            <div class="col-md-4"><div class="card h-100 text-muted"><div class="card-body"><h2 class="h5"><c:out value="${item}"/></h2><span class="badge bg-secondary">Coming soon</span></div></div></div>
        </c:forEach>
    </div>
</main>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
