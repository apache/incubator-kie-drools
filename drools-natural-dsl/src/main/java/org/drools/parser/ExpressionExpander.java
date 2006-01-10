package org.drools.parser;

public interface ExpressionExpander {

    String expandExpression(String expr);
    boolean isExpanded(String expression);
    
}
