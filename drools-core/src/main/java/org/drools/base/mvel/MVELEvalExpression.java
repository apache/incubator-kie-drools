package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELEvalExpression
    implements
    EvalExpression,
    Serializable  {
      

    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELEvalExpression(final Serializable expr,
                              final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public boolean evaluate(final Tuple tuple,
                            final Declaration[] requiredDeclarations,
                            final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( tuple,
                                 null,
                                 null,
                                 workingMemory,
                                 null );
        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                                 new Object(),
                                                                 this.factory );
        return result.booleanValue();
    }

}
