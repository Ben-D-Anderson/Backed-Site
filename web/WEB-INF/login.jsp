<%@ page import="backed.site.enums.Servlets" %>
<%@ page import="backed.site.enums.Parameters" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" type="image/png" href="img/favicon.ico">
<meta charset="ISO-8859-1">
<title>Backed - Login</title>
</head>
<body>
	<form action="<%=pageContext.getServletContext().getContextPath() + Servlets.LOGIN.getUrlPattern()%>" method="POST">
		<input type="text" name="<%=Parameters.Login.USERNAME.getParam()%>" placeholder="Username" required>
		<br>
		<input type="password" name="<%=Parameters.Login.PASSWORD.getParam()%>" placeholder="Password" required>
		<br>
		<button type="submit">Login</button>
		<br>
		Don't have an account yet? <a href="<%=pageContext.getServletContext().getContextPath() + Servlets.REGISTER.getUrlPattern()%>">Register</a>
	</form>
</body>
</html>