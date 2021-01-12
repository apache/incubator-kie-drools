package java.time.format;

import java.time.DateTimeException;

public class DateTimeParseException extends DateTimeException {

    public DateTimeParseException(final String message) {
        super(message);
    }

    public DateTimeParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}