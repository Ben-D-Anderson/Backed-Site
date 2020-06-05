package backed.site.api;

import backed.site.mysql.MySQL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DownloadAPIServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String cookie = (String) req.getAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);

    }

}
