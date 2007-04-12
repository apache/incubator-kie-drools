package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELPredicateExpression
    implements
    PredicateExpression {
    private static final long       serialVersionUID = 320L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELPredicateExpression(final Serializable expr,
                                   final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public boolean evaluate(final Object object,
                            final Tuple tuple,
                            final Declaration[] previousDeclarations,
                            final Declaration[] requiredDeclarations,
                            final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( tuple,
                            object,
                            workingMemory );
        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                           this.factory );
        return result.booleanValue();
    }

}
