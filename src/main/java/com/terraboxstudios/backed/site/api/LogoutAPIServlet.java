package com.terraboxstudios.backed.site.api;

import com.terraboxstudios.backed.site.api.response.Response;
import com.terraboxstudios.backed.site.mysql.MySQL;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutAPIServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        MySQL.getInstance().expireCookie((String) req.getAttribute("auth_cookie"));
        req.removeAttribute("auth_cookie");
        res.setContentType("application/json");
        String jsonResponse = new Gson().toJson(new Response(false, "logout successful, cookie has been invalidated"));
        res.getOutputStream().print(jsonResponse);
    }

}
