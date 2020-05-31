package backed.site.api;

import backed.site.api.response.Response;
import backed.site.mysql.MySQL;
import backed.site.util.FileHandler;
import backed.site.util.Settings;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItem;
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

    private String filePath = System.getProperty("java.io.tmpdir");
    private int maxFileSize = ((int) Settings.getInstance().getConfig().getMaxFileSizeInKB()) * 1000;
    private int maxMemSize = ((int) Settings.getInstance().getConfig().getMaxFileSizeInMemory()) * 1000;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String cookie = (String) req.getAttribute("auth_cookie");
        req.removeAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);

        if (!ServletFileUpload.isMultipartContent(req)) {
            String jsonResponse = new Gson().toJson(new Response(true, "request must be in the format form/multipart"));
            res.getOutputStream().print(jsonResponse);
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(maxMemSize);

        String storageLoc = (String) Settings.getInstance().getConfig().getFileStorageLocation();
        if (!storageLoc.endsWith(File.separator))
            storageLoc += File.separator;
        factory.setRepository(new File(storageLoc));

        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(maxFileSize);

        try {
            List<FileItem> fileItems = upload.parseRequest(req);

            Iterator<FileItem> i = fileItems.iterator();

            while (i.hasNext()) {
                FileItem fi = i.next();
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
