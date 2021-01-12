
package java.time;

public class DateTimeException extends RuntimeException {

    public DateTimeException(final String message) {
        super(message);
    }

    public DateTimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}