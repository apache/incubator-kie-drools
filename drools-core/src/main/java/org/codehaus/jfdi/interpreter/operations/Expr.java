package org.codehaus.jfdi.interpreter.operations;


public interface Expr extends Statement {
    Object getValue();
    Class getType();
}
