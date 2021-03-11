package backed.site.api.exceptions;

public class NoValidEncryptionKeyException extends Exception {

    public NoValidEncryptionKeyException(String message) {
        super(message);
    }

}
