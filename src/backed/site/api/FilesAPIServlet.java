package backed.site.api;

import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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
        for (File file : dir.listFiles()) {
            if (!tmp.toString().isEmpty())
                tmp.append(", ");
            if (file.isDirectory())
                tmp.append("{\"" + file.getName() + "\":[" + listRecursively(file) + "]}");
            else
                tmp.append("\"" + file.getName() + "\"");
        }
        finalJson = "[" + tmp.toString() + "]";
        return tmp.toString();
    }

}
