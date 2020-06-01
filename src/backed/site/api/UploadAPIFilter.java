package backed.site.api;

import backed.site.api.response.Response;
import backed.site.mysql.MySQL;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UploadAPIFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (!req.getMethod().equals("POST")) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "post method required"));
            res.getOutputStream().print(jsonResponse);
            return;
        }
        if (!req.getHeader("User-Agent").equalsIgnoreCase("backed/api")) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "invalid user-agent header"));
            res.getOutputStream().print(jsonResponse);
            return;
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("auth_session")) {
                    if(MySQL.getInstance().registeredCookies.contains(cookie.getValue()) && MySQL.getInstance().isCookieValid(cookie.getValue())) {
                        req.setAttribute("auth_cookie", cookie.getValue());
                        chain.doFilter(req, res);
                        return;
                    }
                }
            }
        }
        res.setContentType("application/json");
        String jsonResponse = new Gson().toJson(new Response(true, "auth_session cookie not valid"));
        res.getOutputStream().print(jsonResponse);
    }

}
