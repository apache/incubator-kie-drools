package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectData;
import org.drools.rule.Package;
import org.drools.spi.FieldValue;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELReturnValueExpression
    implements
    ReturnValueExpression,
    Serializable  {
    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory prototype;

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
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }        

        return org.drools.base.FieldFactory.getFieldValue( MVEL.executeExpression( this.expr,
                                                                                   null,
                                                                                   factory ) );
    }

    public Object createContext() {
        return this.prototype.clone();
    }

}
