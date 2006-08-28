package org.drools.base.dataproviders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class MethodDataProvider
    implements
    DataProvider {
    private final MethodInvoker method;
    
    public MethodDataProvider(MethodInvoker method) {
        this.method = method;
    }
     

    public Declaration[] getRequiredDeclarations() {
        return this.method.getRequiredDeclarations();
    }

    public Iterator getResults(Tuple tuple,
                               WorkingMemory wm,
                               PropagationContext ctx) {
        Object result = this.method.invoke( tuple, wm, ctx );
        if ( result instanceof Collection ) {
            return ((Collection) result).iterator();
        } else if ( result instanceof Iterator ) {
            return (Iterator) result;
        } else {
            List resultAsList = new ArrayList( 1 );
            resultAsList.add( result );
            return resultAsList.iterator();
        }        
    }

}
