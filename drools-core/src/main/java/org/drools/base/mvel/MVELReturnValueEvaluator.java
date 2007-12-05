package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.spi.Action;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ReturnValueEvaluator;
import org.mvel.CompiledExpression;
import org.mvel.MVEL;
import org.mvel.debug.DebugTools;

public class MVELReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    Serializable {
    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELReturnValueEvaluator(final Serializable expr,
                      final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }
    
    public String getDialect() {
        return "mvel";
    }

    public Object evaluate(final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( null,
                                 null,
                                 null,
                                 workingMemory,
                                 null );
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
                                  this.factory );
        } else {
            value = MVEL.executeExpression( compexpr,
                                    null,
                                    this.factory );
        }
        
        if ( !(value instanceof Boolean) ) {
            throw new RuntimeException("Constraints must return boolean values" );
        }
        return ((Boolean)value).booleanValue();        

    }

    public Serializable getCompExpr() {
        return expr;
    }

}
