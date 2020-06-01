package backed.site.api;

import backed.site.api.response.Response;
import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UploadAPIServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String cookie = (String) req.getAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);

        JsonArray jsonArray = new JsonArray();

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            List<FileItem> items = upload.parseRequest(req);

            for (FileItem item : items) {
                InputStream inputStream = item.getInputStream();
                String name = item.getName().replace("..", "");
                FileHandler.encryptAndSaveFile(username, inputStream, name);
                JsonElement jsonElement = new Gson().toJsonTree(new Response(false, "file " + name + " successfully uploaded"));
                jsonArray.add(jsonElement);
            }
        } catch(Exception e) {
            JsonElement jsonElement = new Gson().toJsonTree(new Response(true, "failed to upload file"));
            jsonArray.add(jsonElement);
        }
        res.setContentType("application/json");
        res.getOutputStream().print(jsonArray.toString());
    }

}
