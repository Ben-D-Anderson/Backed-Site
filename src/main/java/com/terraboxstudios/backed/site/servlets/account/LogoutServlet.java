package com.terraboxstudios.backed.site.servlets.account;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.terraboxstudios.backed.site.enums.Pages;
import com.terraboxstudios.backed.site.mysql.MySQL;

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQL.getInstance().expireCookie((String) request.getAttribute("auth_cookie"));
		request.removeAttribute("auth_cookie");
		request.getRequestDispatcher(Pages.LOGOUT.getLoc()).forward(request, response);
	}

}
