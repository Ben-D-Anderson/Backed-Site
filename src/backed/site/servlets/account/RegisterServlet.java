package backed.site.servlets.account;

import backed.site.mailing.Mailing;
import backed.site.mailing.emails.EmailConfirmationEmail;
import backed.site.enums.Pages;
import backed.site.enums.Servlets;
import backed.site.mysql.MySQL;
import backed.site.mysql.MySQLQueue;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(request, response);
		return;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		MySQL.getInstance().createUser(username, email, password);

		String code = MySQL.getInstance().generateConfirmationCode();
		MySQLQueue.getInstance().addToQueue(() -> MySQL.getInstance().createConfirmationCode(username, code));
		Mailing.getInstance().sendEmail(email, new EmailConfirmationEmail(code));
		
		response.sendRedirect(getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
	}

}
