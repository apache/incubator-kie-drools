package org.kie.dmn.feel.runtime.events;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * A base class with common functionality to all events
 */
public class FEELEventBase implements FEELEvent {
    private final Severity severity;
    private final String message;
    private final Throwable sourceException;

    public FEELEventBase(Severity severity, String message, Throwable sourceException) {
        this.severity = severity;
        this.message = message;
        this.sourceException = sourceException;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getSourceException() {
        return sourceException;
    }

    @Override
    public int getLine() {
        return -1;
    }

    @Override
    public int getColumn() {
        return -1;
    }

    @Override
    public Object getOffendingSymbol() {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "severity=" + severity +
               ", message='" + message + '\'' +
               ", sourceException=" + sourceException +
               '}';
    }
}
