package com.terraboxstudios.backed.site.enums;

public enum Pages {

    INDEX("/WEB-INF/index.jsp"),
    LOGIN("/WEB-INF/login.jsp"),
    LOGOUT("/WEB-INF/logout.jsp"),
    REGISTER("/WEB-INF/register.jsp"),
    ERROR("/WEB-INF/error.jsp");

    private String loc;

    Pages(String loc) {
        this.loc = loc;
    }

    public String getLoc() {
        return loc;
    }

}
