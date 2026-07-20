<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Page Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<main class="container min-vh-100 d-flex align-items-center justify-content-center text-center">
    <div>
        <p class="display-1 fw-bold text-primary mb-0">404</p>
        <h1 class="h2">Page not found</h1>
        <p class="text-secondary">The page you requested does not exist.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/">Return home</a>
    </div>
</main>
</body>
</html>
