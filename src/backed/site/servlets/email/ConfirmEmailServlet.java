package backed.site.servlets.email;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import backed.site.enums.Parameters;
import backed.site.enums.Servlets;
import backed.site.mysql.MySQL;
import backed.site.mysql.MySQLQueue;

public class ConfirmEmailServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter(Parameters.ConfirmEmail.CODE.getParam());
		MySQLQueue.getInstance().addToQueue(() -> MySQL.getInstance().confirmEmail(code));
		response.sendRedirect(getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
	}

}
