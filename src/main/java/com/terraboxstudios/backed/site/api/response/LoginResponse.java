package com.terraboxstudios.backed.site.api.response;

import java.util.Calendar;

public class LoginResponse {

    public String error, message;
    public Cookie cookie;

    public LoginResponse(boolean error, String message, javax.servlet.http.Cookie cookie) {
        this.error = String.valueOf(error);
        this.message = message;
        this.cookie = new Cookie(cookie);
    }

    class Cookie {
        public String name, value;
        public long expiry;

        public Cookie(javax.servlet.http.Cookie cookie) {
            this.name = cookie.getName();
            this.value = cookie.getValue();
            this.expiry = Calendar.getInstance().getTimeInMillis() + (cookie.getMaxAge() * 1000);
        }
    }

}
