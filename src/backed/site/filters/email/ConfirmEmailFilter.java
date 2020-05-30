package backed.site.filters.email;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import backed.site.enums.Parameters;
import backed.site.enums.Servlets;
import backed.site.mysql.MySQL;
import backed.site.servlets.email.ConfirmEmailServlet;

public class ConfirmEmailFilter implements Filter {
	
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
		
		String code = req.getParameter(Parameters.ConfirmEmail.CODE.getParam());
		
		if (code == null || code.isEmpty()) {
			res.sendRedirect(req.getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
			return;
		}
		if (code.length() != 36) {
			res.sendRedirect(req.getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
			return;
		}
		if (MySQL.getInstance().getUsernameFromConfirmationCode(code) != null) {
			chain.doFilter(req, res);
		} else {
			res.sendRedirect(req.getServletContext().getContextPath() + Servlets.INDEX.getUrlPattern());
			return;
		}
		
	}

}
