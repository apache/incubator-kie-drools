package org.drools.base.dataproviders;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
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
    Externalizable  {

    private static final long serialVersionUID = 1901006343031798173L;

    private Serializable      expression;
    private DroolsMVELFactory prototype;
    private String id;

    public MVELDataProvider() {

    }

    public MVELDataProvider(final Serializable expression,
                            final DroolsMVELFactory factory,
                            final String id) {
        this.expression = expression;
        this.prototype = factory;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expression  = (Serializable)in.readObject();
        prototype   = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(expression);
        out.writeObject(prototype);
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
        
        if ( result == null ) {
            return Collections.EMPTY_LIST.iterator();
        } else if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else if ( result.getClass().isArray() ) { 
            return new ArrayIterator( result );
        } else {
            return Collections.singletonList( result ).iterator();
        }
    }
}
