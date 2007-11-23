package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.spi.Action;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.mvel.CompiledExpression;
import org.mvel.MVEL;
import org.mvel.debug.DebugTools;

public class MVELAction
    implements
    Action,
    Serializable {
    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELAction(final Serializable expr,
                      final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }
    
    public String getDialect() {
        return "mvel";
    }

    public void execute(final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( null,
                                 null,
                                 null,
                                 workingMemory,
                                 null );
        CompiledExpression compexpr = (CompiledExpression) this.expr;

        //Receive breakpoints from debugger
        MVELDebugHandler.prepare();

        if ( MVELDebugHandler.isDebugMode() ) {
            if ( MVELDebugHandler.verbose ) {
                System.out.println( DebugTools.decompile( compexpr ) );
            }
            MVEL.executeDebugger( compexpr,
                                  null,
                                  this.factory );
        } else {
            MVEL.executeExpression( compexpr,
                                    null,
                                    this.factory );
        }

    }

    public Serializable getCompExpr() {
        return expr;
    }

}
