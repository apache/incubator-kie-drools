package org.drools.base.dataproviders;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.codehaus.jfdi.interpreter.operations.Expr;
import org.codehaus.jfdi.parser.JFDIParser;
import org.drools.WorkingMemory;
import org.drools.base.DroolsJFDIFactory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Declaration;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class JFDIDataProvider
    implements
    DataProvider {
    private final Expr              expression;
    private final DroolsJFDIFactory factory;

    public JFDIDataProvider(final Expr expression,
                            final DroolsJFDIFactory factory) {
        this.expression = expression;
        this.factory = factory;
    }

    public Declaration[] getRequiredDeclarations() {
        return factory.getRequiredDeclarations();
    }

    public Iterator getResults(final Tuple tuple,
                               final WorkingMemory wm,
                               final PropagationContext ctx) {
        factory.setContext( tuple, wm );
        Object result = this.expression.getValue();
        if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else {
            return Collections.singletonList( result ).iterator();
        }
    }
}
