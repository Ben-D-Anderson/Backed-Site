package com.terraboxstudios.backed.site.servlets.account;

import com.terraboxstudios.backed.site.enums.Pages;
import com.terraboxstudios.backed.site.enums.Parameters;
import com.terraboxstudios.backed.site.enums.Servlets;
import com.terraboxstudios.backed.site.mysql.MySQL;
import com.terraboxstudios.backed.site.mysql.MySQLService;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(Pages.LOGIN.getLoc()).forward(request, response);
		return;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter(Parameters.Login.USERNAME.getParam());
		String pass = request.getParameter(Parameters.Login.PASSWORD.getParam());
		String rememberMe = request.getParameter(Parameters.Login.REMEMBER_ME.getParam());
		if (user == null ||
				pass == null ||
				user.isEmpty() ||
				pass.isEmpty()) {
			request.getRequestDispatcher(Pages.LOGIN.getLoc()).forward(request, response);
			return;
		}
		
		if (MySQL.getInstance().checkLogin(user, pass)) {
			Cookie cookie = MySQL.getInstance().generateCookie();
			if (!(rememberMe != null && rememberMe.equalsIgnoreCase("true")))
				cookie.setMaxAge(3600);
			response.addCookie(cookie);
			MySQLService.getInstance().addToQueue(() -> MySQL.getInstance().setCookie(user, cookie));
			response.sendRedirect(getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
		} else {
			request.getRequestDispatcher(Pages.LOGIN.getLoc()).forward(request, response);
			return;
		}
		
	}

}
