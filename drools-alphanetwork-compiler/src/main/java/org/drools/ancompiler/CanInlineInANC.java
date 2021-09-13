package org.drools.ancompiler;

import com.github.javaparser.ast.expr.Expression;

public interface CanInlineInANC<T> {

    /*
        This is the expression to initialise the inline form
        This will be inlined directly inside the Compiled Alpha Network
     */
    Expression toANCInlinedForm();

    /*
        The type returned from the inline expression
     */
    Class<T> inlinedType();

}
