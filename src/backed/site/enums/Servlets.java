package backed.site.enums;

public enum Servlets {

    INDEX("/"),
    LOGIN("/login"),
    LOGOUT("/logout"),
    REGISTER("/register"),
    CONFIRM_EMAIL("/confirm_email"),
    ERROR("/error");

    private String urlPattern;

    Servlets(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getUrlPattern() {
        return urlPattern;
    }
}
