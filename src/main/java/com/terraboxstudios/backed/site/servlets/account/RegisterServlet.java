package com.terraboxstudios.backed.site.servlets.account;

import com.terraboxstudios.backed.site.enums.Pages;
import com.terraboxstudios.backed.site.enums.Servlets;
import com.terraboxstudios.backed.site.mailing.Mailing;
import com.terraboxstudios.backed.site.mailing.emails.EmailConfirmationEmail;
import com.terraboxstudios.backed.site.mysql.MySQL;
import com.terraboxstudios.backed.site.mysql.MySQLService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		MySQL.getInstance().createUser(username, email, password);

		String code = MySQL.getInstance().generateConfirmationCode();
		MySQLService.getInstance().addToQueue(() -> MySQL.getInstance().createConfirmationCode(username, code));
		Mailing.getInstance().sendEmail(email, new EmailConfirmationEmail(code));
		
		response.sendRedirect(getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
	}

}
