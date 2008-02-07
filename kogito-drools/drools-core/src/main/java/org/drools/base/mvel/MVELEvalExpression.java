package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectData;
import org.drools.rule.Package;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

public class MVELEvalExpression
    implements
    EvalExpression,
    Serializable  {
      

    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory prototype;

    public MVELEvalExpression(final Serializable expr,
                              final DroolsMVELFactory factory) {
        this.expr = expr;
        this.prototype = factory;
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
        
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }        
        
        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                                 new Object(),
                                                                 factory );
        return result.booleanValue();
    }

}
