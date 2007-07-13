package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.FieldValue;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELReturnValueExpression
    implements
    ReturnValueExpression,
    Serializable  {
    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELReturnValueExpression(final Serializable expr,
                                     final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public FieldValue evaluate(final Object object,
                               final Tuple tuple,
                               final Declaration[] previousDeclarations,
                               final Declaration[] requiredDeclarations,
                               final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( tuple,
                                 null,
                                 object,
                                 workingMemory,
                                 null );

        return org.drools.base.FieldFactory.getFieldValue( MVEL.executeExpression( this.expr,
                                                                                   null,
                                                                                   this.factory ) );
    }

}
