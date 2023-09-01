package org.kie.dmn.feel.runtime.events;

import org.antlr.v4.runtime.RecognitionException;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

/**
 * An event class to report a syntax error as returned by the parser
 */
public class SyntaxErrorEvent extends FEELEventBase implements FEELEvent {

    private final int line;
    private final int column;
    private final Object offendingSymbol;

    public SyntaxErrorEvent(Severity severity, String msg, RecognitionException e, int line, int charPositionInLine, Object offendingSymbol) {
        super( severity, msg, e );
        this.line = line;
        this.column = charPositionInLine;
        this.offendingSymbol = offendingSymbol;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Object getOffendingSymbol() {
        return offendingSymbol;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "severity=" + getSeverity() +
               ", line=" + line +
               ", column=" + column +
               ", offendingSymbol=" + offendingSymbol +
               ", message='" + getMessage() + '\'' +
               ", sourceException=" + getSourceException() +
               '}';
    }
}
