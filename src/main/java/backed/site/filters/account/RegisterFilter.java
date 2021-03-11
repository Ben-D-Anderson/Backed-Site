package backed.site.filters.account;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import backed.site.enums.Pages;
import backed.site.enums.Servlets;
import backed.site.mysql.MySQL;
import backed.site.util.Utils;

public class RegisterFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("auth_session")) {
					if(MySQL.getInstance().registeredCookies.contains(cookie.getValue()) && MySQL.getInstance().isCookieValid(cookie.getValue())) {
						res.sendRedirect(req.getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
						return;
					}
				}
			}
		}

		if (req.getMethod().equals("POST")) {
			if (req.getParameter("password") == null ||
					req.getParameter("username") == null ||
					req.getParameter("password_repeat") == null ||
					req.getParameter("email") == null) {
				req.getSession().setAttribute("error", "empty-details");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}
			if ((req.getParameter("password").length() < 8 || req.getParameter("password").length() > 20) ||
					(req.getParameter("password_repeat").length() < 8 || req.getParameter("password_repeat").length() > 20) ||
					(req.getParameter("username").length() < 4 || req.getParameter("username").length() > 18) ||
					(req.getParameter("email").length() < 4 || req.getParameter("email").length() > 80)) {
				req.getSession().setAttribute("error", "basic-rules-not-met");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}
			Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(req.getParameter("username"));
			boolean badCharsInUsername = m.find();

			if (badCharsInUsername) {
				req.getSession().setAttribute("error", "bad-chars-in-name");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}

			boolean validEmail = Utils.isValidEmail(req.getParameter("email"));
			if (!validEmail) {
				req.getSession().setAttribute("error", "bad-email");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}

			if (!checkString(req.getParameter("password"))) {
				req.getSession().setAttribute("error", "advanced-rules-not-met");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}
			if (!req.getParameter("password").equals(req.getParameter("password_repeat"))) {
				req.getSession().setAttribute("error", "passwords-dont-match");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}

			if(MySQL.getInstance().getEmailFromUsername(req.getParameter("username")) != null) {
				req.getSession().setAttribute("error", "user-already-exists");
				req.getRequestDispatcher(Pages.REGISTER.getLoc()).forward(req, res);
				return;
			}
		}

		chain.doFilter(req, res);
	}

	private boolean checkString(String str) {
		char ch;
		boolean capitalFlag = false;
		boolean lowerCaseFlag = false;
		boolean numberFlag = false;
		for(int i=0;i < str.length();i++) {
			ch = str.charAt(i);
			if( Character.isDigit(ch)) {
				numberFlag = true;
			}
			else if (Character.isUpperCase(ch)) {
				capitalFlag = true;
			} else if (Character.isLowerCase(ch)) {
				lowerCaseFlag = true;
			}
			if(numberFlag && capitalFlag && lowerCaseFlag)
				return true;
		}
		return false;
	}

}
