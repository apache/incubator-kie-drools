package org.drools.base.mvel;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELEvalExpression
    implements
    EvalExpression,
    Externalizable  {


    private static final long       serialVersionUID = 400L;

    private Serializable      expr;
    private DroolsMVELFactory prototype;
    private String id;

    public MVELEvalExpression() {
    }

    public MVELEvalExpression(final Serializable expr,
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

    public boolean evaluate(final Tuple tuple,
                            final Declaration[] requiredDeclarations,
                            final WorkingMemory workingMemory,
                            final Object context) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) context;
        factory.setContext( tuple,
                                 null,
                                 null,
                                 workingMemory,
                                 null );

        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                                 new Object(),
                                                                 factory );
        return result.booleanValue();
    }

}
