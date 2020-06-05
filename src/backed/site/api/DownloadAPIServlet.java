package backed.site.api;

import backed.site.api.exceptions.NoValidEncryptionKeyException;
import backed.site.api.response.Response;
import backed.site.enums.Parameters;
import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class DownloadAPIServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String cookie = (String) req.getAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);
        String filename = (String) req.getAttribute("file_name");

        try {
            res.setContentType("text/plain");
            FileHandler.decryptToOutputStream(username, filename, res.getOutputStream());
            res.setHeader("Content-disposition", "attachment; filename=" + filename);
        } catch (IOException e) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "error occurred writing file to output stream"));
            res.getOutputStream().print(jsonResponse);
        } catch (GeneralSecurityException e) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "error occurred decrypting file"));
            res.getOutputStream().print(jsonResponse);
        } catch (NoValidEncryptionKeyException e) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "error occurred fetching decryption key for user"));
            res.getOutputStream().print(jsonResponse);
        }

    }

}
