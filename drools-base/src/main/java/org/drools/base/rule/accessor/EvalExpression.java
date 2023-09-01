package org.drools.base.rule.accessor;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;

public interface EvalExpression
    extends
    Invoker,
    Cloneable {
    
    Object createContext();
    
    boolean evaluate(BaseTuple tuple,
                     Declaration[] requiredDeclarations,
                     ValueResolver valueResolver,
                     Object context ) throws Exception;

    void replaceDeclaration(Declaration declaration,
                            Declaration resolved);

    EvalExpression clone();

    default EvalExpression clonePreservingDeclarations(EvalExpression original) {
        return original;
    }
}
