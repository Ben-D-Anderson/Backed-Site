<%@ page import="backed.site.enums.Servlets" %>
<%@ page import="backed.site.enums.Parameters" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" type="image/png" href="img/favicon.ico">
<meta charset="ISO-8859-1">
<title>Backed - Register</title>
</head>	
<body>
<!--
TODO: HANDLING OF THE ERROR SESSION ATTRIBUTE IF REGISTRATION FAILS
-->
<form action="<%=pageContext.getServletContext().getContextPath() + Servlets.REGISTER.getUrlPattern()%>" method="POST">
		<input type="text" name="<%=Parameters.Register.USERNAME.getParam()%>" placeholder="Username" required>
		<br>
		<input type="email" name="<%=Parameters.Register.EMAIL.getParam()%>" placeholder="Email Address" required>
		<br>
		<input type="password" name="<%=Parameters.Register.PASSWORD.getParam()%>" placeholder="Password" required>
		<br>
		<input type="password" name="<%=Parameters.Register.PASSWORD_REPEAT.getParam()%>" placeholder="Confirm Password" required>
		<br>
		<button type="submit">Register</button>
		<br>
		 Already have an account? Click <a href="<%=pageContext.getServletContext().getContextPath() + Servlets.LOGIN.getUrlPattern()%>">here</a>
	</form>
</body>
</html>