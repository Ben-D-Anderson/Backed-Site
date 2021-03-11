package com.terraboxstudios.backed.site.api;

import com.terraboxstudios.backed.site.api.response.Response;
import com.terraboxstudios.backed.site.mysql.MySQL;
import com.terraboxstudios.backed.site.util.FileHandler;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteAPIServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String cookie = (String) req.getAttribute("auth_cookie");
        String username = MySQL.getInstance().getUsernameFromCookie(cookie);
        String filename = (String) req.getAttribute("file_name");

        File file = FileHandler.getOutputFile(username, filename);
        boolean exists = file.exists();
        boolean deleted = file.delete();

        if (deleted) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(false, "file '" + filename + "' successfully deleted"));
            res.getOutputStream().print(jsonResponse);
            return;
        }
        if (exists) {
            res.setContentType("application/json");
            String jsonResponse = new Gson().toJson(new Response(true, "file '" + filename + "' exists but could not be deleted"));
            res.getOutputStream().print(jsonResponse);
            return;
        }
        res.setContentType("application/json");
        String jsonResponse = new Gson().toJson(new Response(true, "file '" + filename + "' doesn't exist"));
        res.getOutputStream().print(jsonResponse);
        return;

    }

}
