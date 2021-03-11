package com.terraboxstudios.backed.site.servlets;

import com.terraboxstudios.backed.site.enums.Pages;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(Pages.INDEX.getLoc()).forward(request, response);
		return;
	}

}
