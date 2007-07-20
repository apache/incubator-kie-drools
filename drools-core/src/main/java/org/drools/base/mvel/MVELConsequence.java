package org.drools.base.mvel;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.mvel.CompiledExpression;
import org.mvel.MVEL;
import org.mvel.MVELRuntime;

public class MVELConsequence
    implements
    Consequence,
    Serializable  {
    private static final long       serialVersionUID = 400L;

    private final Serializable      expr;
    private final DroolsMVELFactory factory;

    public MVELConsequence(final Serializable expr,
                           final DroolsMVELFactory factory) {
        this.expr = expr;
        this.factory = factory;
    }

    public void evaluate(final KnowledgeHelper knowledgeHelper,
                         final WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( knowledgeHelper.getTuple(),
                                 knowledgeHelper,
                                 null,
                                 workingMemory,
                                 null );
        CompiledExpression compexpr = (CompiledExpression)this.expr;

        //MVEL:for testing, we can have at least one breakpoint
        //MVELRuntime.registerBreakpoint( compexpr.getSourceName(), 1 );

        //Receive breakpoints from debugger
        MVELDebugHandler.prepare();
        
		//we are always debugging for now, but we should either debug or run
        MVEL.executeDebugger( compexpr, null, this.factory);

        /*MVEL.executeExpression( this.expr,
                                null,
                                this.factory );*/
    }

}
