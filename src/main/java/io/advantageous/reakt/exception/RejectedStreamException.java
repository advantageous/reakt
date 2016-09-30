package io.advantageous.reakt.exception;

public class RejectedStreamException extends RuntimeException {

    public RejectedStreamException() {
        super();
    }

    public RejectedStreamException(String message) {
        super(message);
    }

    public RejectedStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejectedStreamException(Throwable cause) {
        super(cause);
    }

    protected RejectedStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
