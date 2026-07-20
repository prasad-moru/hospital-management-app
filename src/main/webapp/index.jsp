<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Online Hospital Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<main>
    <section class="hero-section d-flex align-items-center">
        <div class="container py-5">
            <div class="row align-items-center g-5">
                <div class="col-lg-7">
                    <span class="badge rounded-pill text-bg-light text-primary mb-3">Reliable digital healthcare</span>
                    <h1 class="display-4 fw-bold">Online Hospital Management System</h1>
                    <p class="lead mt-3 mb-4">
                        A centralized platform designed to simplify hospital operations and improve the
                        experience of patients, doctors, and administrative staff.
                    </p>
                    <% if (session.getAttribute("authenticatedUser") == null) { %>
                    <a class="btn btn-light btn-lg px-4" href="${pageContext.request.contextPath}/login">Login</a>
                    <% } else { %>
                    <a class="btn btn-light btn-lg px-4" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                    <form class="d-inline" method="post" action="${pageContext.request.contextPath}/logout"><input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><button class="btn btn-outline-light btn-lg">Logout</button></form>
                    <% } %>
                </div>
                <div class="col-lg-5">
                    <div class="feature-card p-4 p-md-5">
                        <h2 class="h4">One connected system</h2>
                        <p class="mb-0 text-secondary">
                            Future modules will support appointments, medical records, prescriptions,
                            billing, admissions, inventory, reporting, and more.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
