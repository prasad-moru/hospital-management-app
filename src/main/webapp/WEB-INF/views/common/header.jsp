<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/">
                Hospital Management
            </a>
            <div class="d-flex align-items-center gap-3"><c:if test="${sessionScope.authenticatedUser.roleName=='ADMIN'||sessionScope.authenticatedUser.roleName=='DOCTOR'||sessionScope.authenticatedUser.roleName=='PATIENT'}"><a class="text-white text-decoration-none" href="${pageContext.request.contextPath}/medical-records">Medical Records</a></c:if><span class="navbar-text text-white-50 d-none d-md-inline">Care through technology</span></div>
        </div>
    </nav>
</header>
