package com.terraboxstudios.backed.site.filters.account;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.terraboxstudios.backed.site.enums.Servlets;
import com.terraboxstudios.backed.site.mysql.MySQL;

public class LoginFilter implements Filter {

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
		chain.doFilter(req, res);
	}

}
