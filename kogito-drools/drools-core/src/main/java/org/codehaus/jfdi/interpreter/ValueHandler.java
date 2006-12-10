package org.codehaus.jfdi.interpreter;

import org.codehaus.jfdi.interpreter.operations.Expr;

/**
 * This specifies a container for a variable that will be used at runtime. 
 * The runtime will call this when executing.
 */
public interface ValueHandler extends Expr {
    void setValue(Object value);
    
    boolean isLocal();
    boolean isFinal();
    boolean isLiteral();
    
}
