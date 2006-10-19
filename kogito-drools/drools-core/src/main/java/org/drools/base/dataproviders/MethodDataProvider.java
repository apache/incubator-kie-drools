package org.drools.base.dataproviders;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class MethodDataProvider
    implements
    DataProvider {
    private final Invoker invoker;

    public MethodDataProvider(final Invoker invoker) {
        this.invoker = invoker;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.invoker.getRequiredDeclarations();
    }

    public Iterator getResults(final Tuple tuple,
                               final WorkingMemory wm,
                               final PropagationContext ctx) {
        final Object result = this.invoker.invoke( tuple,
                                             wm,
                                             ctx );
        if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else {
            return Collections.singletonList( result ).iterator();
        }
    }

}
