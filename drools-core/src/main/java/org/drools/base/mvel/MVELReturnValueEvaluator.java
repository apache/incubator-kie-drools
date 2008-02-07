package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.rule.MVELDialectData;
import org.drools.rule.Package;
import org.drools.spi.ReturnValueEvaluator;
import org.mvel.compiler.CompiledExpression;
import org.mvel.MVEL;
import org.mvel.debug.DebugTools;

public class MVELReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    Serializable {
    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory prototype;

    public MVELReturnValueEvaluator(final Serializable expr,
                                    final DroolsMVELFactory factory) {
        this.expr = expr;
        this.prototype = factory;
    }

    public String getDialect() {
        return "mvel";
    }

    public Object evaluate(final WorkingMemory workingMemory) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) this.prototype.clone();
        factory.setContext( null,
                            null,
                            null,
                            workingMemory,
                            null );
        
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }
        
        CompiledExpression compexpr = (CompiledExpression) this.expr;

        //Receive breakpoints from debugger
        MVELDebugHandler.prepare();

        Object value;
        if ( MVELDebugHandler.isDebugMode() ) {
            if ( MVELDebugHandler.verbose ) {
                System.out.println( DebugTools.decompile( compexpr ) );
            }
            value = MVEL.executeDebugger( compexpr,
                                          null,
                                          factory );
        } else {
            value = MVEL.executeExpression( compexpr,
                                            null,
                                            factory );
        }

        if ( !(value instanceof Boolean) ) {
            throw new RuntimeException( "Constraints must return boolean values" );
        }
        return ((Boolean) value).booleanValue();

    }

    public Serializable getCompExpr() {
        return expr;
    }

}
