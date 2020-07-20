package backed.site.api;

import backed.site.api.response.LoginResponse;
import backed.site.api.response.Response;
import backed.site.enums.Parameters;
import backed.site.mysql.MySQL;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginAPIServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String user = req.getParameter(Parameters.Login.USERNAME.getParam());
        String pass = req.getParameter(Parameters.Login.PASSWORD.getParam());
        String rememberMe = req.getParameter(Parameters.Login.REMEMBER_ME.getParam());
        if (MySQL.getInstance().checkLogin(user, pass)) {
            Cookie cookie = MySQL.getInstance().generateCookie();
            if (!(rememberMe != null && rememberMe.equalsIgnoreCase("true")))
                cookie.setMaxAge(3600);
            MySQL.getInstance().setCookie(user, cookie);
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new LoginResponse(false, "login successful", cookie));
            res.getOutputStream().print(jsonResponse);
        } else {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "invalid credentials"));
            res.getOutputStream().print(jsonResponse);
        }
    }

}
