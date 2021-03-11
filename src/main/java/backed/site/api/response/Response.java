package backed.site.api.response;

public class Response {

    public String error, message;

    public Response(boolean error, String message) {
        this.error = String.valueOf(error);
        this.message = message;
    }

}
