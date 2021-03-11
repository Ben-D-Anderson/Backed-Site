<%@ page import="com.terraboxstudios.backed.site.enums.Servlets" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Backed</title>
</head>
<body>
	Backed
	<br>
	<br>
	<a href="<%=pageContext.getServletContext().getContextPath() + Servlets.LOGIN.getUrlPattern()%>">Login</a>
	<br>
	<a href="<%=pageContext.getServletContext().getContextPath() + Servlets.REGISTER.getUrlPattern()%>">Register</a>
	<br>
	<a href="<%=pageContext.getServletContext().getContextPath() + Servlets.LOGOUT.getUrlPattern()%>">Logout</a>
</body>
</html>