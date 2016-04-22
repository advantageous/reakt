package io.advantageous.reakt.exception;


public class RejectedPromiseException extends RuntimeException {

    public RejectedPromiseException(String s) {
        super(s);
    }

    public RejectedPromiseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejectedPromiseException(Throwable cause) {
        super(cause);
    }
}
