package backed.site.api;

import backed.site.api.exceptions.NoValidEncryptionKeyException;
import backed.site.api.response.Response;
import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class FileHashAPIServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String cookie = (String) req.getAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);
        String filename = (String) req.getAttribute("file_name");

        res.setContentType("application/json");
        String jsonResponse;

        try {
            String hash = FileHandler.getDecryptedFileHash(username, filename, MessageDigest.getInstance("SHA256"));
            jsonResponse = new Gson().toJson(new Response(false, "hash=" + hash));
        } catch (FileNotFoundException e) {
            jsonResponse = new Gson().toJson(new Response(true, "file '" + filename + "' doesn't exist"));
        } catch (GeneralSecurityException e) {
            jsonResponse = new Gson().toJson(new Response(true, "error occurred decrypting file"));
        } catch (NoValidEncryptionKeyException e) {
            jsonResponse = new Gson().toJson(new Response(true, "error occurred fetching decryption key for user"));
        }
        res.getOutputStream().print(jsonResponse);

    }

}
