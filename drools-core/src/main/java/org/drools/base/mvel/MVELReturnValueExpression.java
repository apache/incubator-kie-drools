package org.drools.base.mvel;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.FieldValue;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class MVELReturnValueExpression
    implements
    ReturnValueExpression,
    Externalizable  {
    private static final long       serialVersionUID = 400L;

    private Serializable      expr;
    private DroolsMVELFactory prototype;

    public MVELReturnValueExpression() {
    }

    public MVELReturnValueExpression(final Serializable expr,
                                     final DroolsMVELFactory factory) {
        this.expr = expr;
        this.prototype = factory;
    }

    public FieldValue evaluate(final Object object,
                               final Tuple tuple,
                               final Declaration[] previousDeclarations,
                               final Declaration[] requiredDeclarations,
                               final WorkingMemory workingMemory,
                               final Object ctx ) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) ctx;
        factory.setContext( tuple,
                                 null,
                                 object,
                                 workingMemory,
                                 null );

        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return org.drools.base.FieldFactory.getFieldValue( MVEL.executeExpression( this.expr,
                                                                                   null,
                                                                                   factory ) );
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

}
