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
import org.drools.util.ArrayIterator;
import org.mvel.MVEL;

public class MVELDataProvider
    implements
    DataProvider,
    Serializable  {

    private static final long serialVersionUID = 1901006343031798173L;
    
    private final Serializable      expression;
    private final DroolsMVELFactory prototype;

    public MVELDataProvider(final Serializable expression,
                            final DroolsMVELFactory factory) {
        this.expression = expression;
        this.prototype = factory;
    }

    public Declaration[] getRequiredDeclarations() {
        return new Declaration[]{};
        //return factory.getRequiredDeclarations();
    }
    
    public Object createContext() {
        return this.prototype.clone();
    }

    public Iterator getResults(final Tuple tuple,
                               final WorkingMemory wm,
                               final PropagationContext ctx,
                               final Object executionContext ) {
        DroolsMVELFactory factory = (DroolsMVELFactory) executionContext;
        
        factory.setContext( tuple,
                                 null,
                                 null,
                                 wm,
                                 null );

        //this.expression.
        final Object result = MVEL.executeExpression( this.expression,
                                                      factory );                
        
        if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else if ( result.getClass().isArray() ) { 
        	return new ArrayIterator( result );
        } else if( result != null ){
            return Collections.singletonList( result ).iterator();
        } else {
            return Collections.EMPTY_LIST.iterator();
        }
    }
}
