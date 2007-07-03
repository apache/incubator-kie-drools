package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.spi.Salience;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELSalienceExpression
    implements
    Salience,
    Serializable {

    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELSalienceExpression(final Serializable expr,
                                  final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public int getValue(final Tuple tuple,
                        final WorkingMemory workingMemory) {
        this.factory.setContext( tuple,
                                 null,
                                 null,
                                 workingMemory );
        return ((Integer) MVEL.executeExpression( this.expr,
                                                  this.factory )).intValue();
    }

}
