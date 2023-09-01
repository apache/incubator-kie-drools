package org.kie.dmn.feel.runtime.events;

import org.kie.dmn.feel.lang.ast.ASTNode;

public class ASTHeuristicCheckEvent extends ASTEventBase {

    public ASTHeuristicCheckEvent(Severity severity, String message, ASTNode astNode) {
        super(severity, message, astNode, null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ASTHeuristicCheckEvent [severity=").append(severity)
        .append(", message=").append(message)
        .append(", sourceException=").append(sourceException)
        .append(", astNode=").append(astNode)
        .append("]");
        return builder.toString();
    }

    
}
