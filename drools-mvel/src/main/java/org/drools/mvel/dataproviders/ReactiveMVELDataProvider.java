package org.drools.mvel.dataproviders;

import java.util.Iterator;

import org.drools.base.base.ValueResolver;
import org.drools.base.phreak.ReactiveObject;
import org.drools.base.reteoo.BaseTuple;
import org.drools.mvel.expr.MVELCompilationUnit;

public class ReactiveMVELDataProvider extends MVELDataProvider {

    public ReactiveMVELDataProvider() { }

    public ReactiveMVELDataProvider( MVELCompilationUnit unit, String id) {
        super(unit, id);
    }

    @Override
    public boolean isReactive() {
        return true;
    }

    @Override
    public Iterator getResults( final BaseTuple tuple,
                                final ValueResolver valueResolver,
                                final Object executionContext ) {
        Object result = evaluate( tuple, valueResolver );
        if (result instanceof ReactiveObject) {
            ( (ReactiveObject) result ).addTuple(tuple);
        }
        return asIterator( result );
    }
}
