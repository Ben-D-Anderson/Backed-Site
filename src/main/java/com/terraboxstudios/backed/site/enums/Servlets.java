package com.terraboxstudios.backed.site.enums;

public enum Servlets {

    INDEX("/"),
    LOGIN("/login"),
    LOGOUT("/logout"),
    REGISTER("/register"),
    CONFIRM_EMAIL("/confirm_email"),
    ERROR("/error"),
    API_LOGIN("/api/login"),
    API_LOGOUT("/api/logout"),
    API_UPLOAD("/api/upload"),
    API_DOWNLOAD("/api/download"),
    API_HASH("/api/hash");

    private final String urlPattern;

    Servlets(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getUrlPattern() {
        return urlPattern;
    }
}
