package backed.site.api;

import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FilesAPIServlet extends HttpServlet {

    private String finalJson;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String cookie = (String) req.getAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);

        File userdir = FileHandler.getOutputDirOfUser(username);
        listRecursively(userdir);

        res.setContentType("application/json");
        res.getOutputStream().print(finalJson);
        return;
    }

    private String listRecursively(File dir) {
        StringBuilder tmp = new StringBuilder();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            long lastModified = file.lastModified();
            if (!tmp.toString().isEmpty())
                tmp.append(", ");
            if (file.isDirectory())
                tmp.append("{\"type\":\"directory\", \"name\":\"" + file.getName() + "\", \"last_modified\": " + lastModified + ", \"files\": [" + listRecursively(file) + "]}");
            else
                tmp.append("{\"type\":\"file\", \"name\":\"" + file.getName().replace(".bak", "") + "\", \"last_modified\": " + lastModified + "}");
        }
        finalJson = "{\"error\":\"false\", \"message\":\"files listed successfully\", \"files\":[" + tmp.toString() + "]}";
        return tmp.toString();
    }

}
