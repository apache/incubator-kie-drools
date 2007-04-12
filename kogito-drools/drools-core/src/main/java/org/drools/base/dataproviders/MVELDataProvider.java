package org.drools.base.dataproviders;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.rule.Declaration;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELDataProvider
    implements
    DataProvider {
    //private final Expr              expression;
    private final Serializable      expression;
    private final DroolsMVELFactory factory;

    public MVELDataProvider(final Serializable expression,
                            final DroolsMVELFactory factory) {
        this.expression = expression;
        this.factory = factory;
    }

    public Declaration[] getRequiredDeclarations() {
        return new Declaration[]{};
        //return factory.getRequiredDeclarations();
    }

    public Iterator getResults(final Tuple tuple,
                               final WorkingMemory wm,
                               final PropagationContext ctx) {
        this.factory.setContext( tuple,
                            null,
                            wm );

        //this.expression.
        final Object result = MVEL.executeExpression( this.expression,
                                                this.factory );
        if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else {
            return Collections.singletonList( result ).iterator();
        }
    }
}
