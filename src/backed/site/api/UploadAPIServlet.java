package backed.site.api;

import backed.site.api.response.Response;
import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;
import backed.site.util.Settings;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class UploadAPIServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String filePath = System.getProperty("java.io.tmpdir");
        int maxFileSize = Integer.parseInt(Settings.getInstance().getConfig().getMaxFileSizeInKB().toString()) * 1000;
        int maxMemSize = Integer.parseInt(Settings.getInstance().getConfig().getMaxFileSizeInMemory().toString()) * 1000;
        String cookie = (String) req.getAttribute("auth_cookie");
        req.removeAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);

        if (!ServletFileUpload.isMultipartContent(req)) {
            String jsonResponse = new Gson().toJson(new Response(true, "request must be in the format multipart/form-data"));
            res.getOutputStream().print(jsonResponse);
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(maxMemSize);

        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(maxFileSize);

        try {
            List<FileItem> fileItems = upload.parseRequest(req); //this line causes error which isnt output

            for (FileItem fi : fileItems) {
                if (!fi.isFormField()) {
                    String fileName = fi.getName();
                    while (fileName.contains(".."))
                        fileName = fileName.replace("..", "");

                    File file;
                    if (fileName.lastIndexOf(File.separator) >= 0) {
                        file = new File(filePath + File.separator + fileName.substring(fileName.lastIndexOf(File.separator)));
                    } else {
                        file = new File(filePath + File.separator + fileName.substring(fileName.lastIndexOf(File.separator) + 1));
                    }
                    fi.write(file);
                    FileHandler.encryptAndSaveFile(username, file);
                    file.delete();
                    String jsonResponse = new Gson().toJson(new Response(false, "file " + fileName + " uploaded"));
                    res.getOutputStream().print(jsonResponse);
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            String jsonResponse = new Gson().toJson(new Response(true, ex.getMessage()));
            res.getOutputStream().print(jsonResponse);
        }
    }

}
