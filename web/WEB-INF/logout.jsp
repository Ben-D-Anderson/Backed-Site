<%@ page import="com.terraboxstudios.backed.site.enums.Servlets" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<link rel="icon" type="image/png" href="img/favicon.ico">
<meta charset="ISO-8859-1">
<title>Backed - Logout</title>
</head>
<body>
	You Have Been Logged Out.
	<br>
	<a href="<%=pageContext.getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern() %>">Home</a>
</body>
</html>