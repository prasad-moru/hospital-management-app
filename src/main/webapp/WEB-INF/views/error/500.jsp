<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Server Error</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<main class="container min-vh-100 d-flex align-items-center justify-content-center text-center">
    <div>
        <p class="display-1 fw-bold text-danger mb-0">500</p>
        <h1 class="h2">Something went wrong</h1>
        <p class="text-secondary">The server could not complete your request. Please try again later.</p>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/">Return home</a>
    </div>
</main>
</body>
</html>
