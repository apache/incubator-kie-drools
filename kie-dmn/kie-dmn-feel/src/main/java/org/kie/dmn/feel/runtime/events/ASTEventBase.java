package org.kie.dmn.feel.runtime.events;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.ast.ASTNode;

/**
 * A base class with common functionality to all events
 */
public class ASTEventBase implements FEELEvent {

    protected final Severity severity;
    protected final String message;
    protected final ASTNode astNode;
    protected Throwable sourceException;
    
    public ASTEventBase(Severity severity, String message, ASTNode astNode, Throwable sourceException) {
        this(severity, message, astNode);
        this.sourceException = sourceException;
    }

    public ASTEventBase(Severity severity, String message, ASTNode astNode) {
        this.severity = severity;
        this.message = message;
        this.astNode = astNode;
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
        StringBuilder builder = new StringBuilder();
        builder.append("ASTEventBase [severity=").append(severity)
        .append(", message=").append(message)
        .append(", sourceException=").append(sourceException)
        .append(", astNode=").append(astNode)
        .append("]");
        return builder.toString();
    }

    
}
