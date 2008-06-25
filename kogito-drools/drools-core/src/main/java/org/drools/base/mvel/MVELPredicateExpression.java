package org.drools.base.mvel;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class MVELPredicateExpression implements PredicateExpression {
    private static final long       serialVersionUID = 400L;

    private Serializable      expr;
    private DroolsMVELFactory prototype;
    private String id;

    public MVELPredicateExpression() {
    }

    public MVELPredicateExpression(final Serializable expr,
                                   final DroolsMVELFactory factory,
                                   final String id) {
        this.expr = expr;
        this.prototype = factory;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expr    = (Serializable)in.readObject();
        prototype   = (DroolsMVELFactory)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(expr);
        out.writeObject(prototype);
    }

    public Object createContext() {
        return this.prototype.clone();
    }

    public boolean evaluate(final Object object,
                            final Tuple tuple,
                            final Declaration[] previousDeclarations,
                            final Declaration[] requiredDeclarations,
                            final WorkingMemory workingMemory,
                            final Object context ) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) context;
        factory.setContext( tuple,
                                 null,
                                 object,
                                 workingMemory,
                                 null );                

        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                                 object,
                                                                 factory );
        return result.booleanValue();
    }

}
