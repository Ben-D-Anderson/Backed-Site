package com.terraboxstudios.backed.site.servlets.email;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.terraboxstudios.backed.site.enums.Parameters;
import com.terraboxstudios.backed.site.enums.Servlets;
import com.terraboxstudios.backed.site.mysql.MySQL;
import com.terraboxstudios.backed.site.mysql.MySQLService;

public class ConfirmEmailServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter(Parameters.ConfirmEmail.CODE.getParam());
		MySQLService.getInstance().addToQueue(() -> MySQL.getInstance().confirmEmail(code));
		response.sendRedirect(getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
	}

}
