package org.drools.base.mvel;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectData;
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

    public MVELPredicateExpression() {
    }

    public MVELPredicateExpression(final Serializable expr,
                                   final DroolsMVELFactory factory) {
        this.expr = expr;
        this.prototype = factory;
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

        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                                 object,
                                                                 factory );
        return result.booleanValue();
    }

}
