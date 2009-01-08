package org.drools.runtime.pipeline.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.pipeline.Action;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class MvelAction extends BaseEmitter
    implements
    Action,
    Receiver {
    private Serializable expr;

    public MvelAction(String text) {
        final ParserContext parserContext = new ParserContext();
        parserContext.setStrictTypeEnforcement( false );

        ExpressionCompiler compiler = new ExpressionCompiler( text );
        this.expr = compiler.compile( );
    }

    public void receive(Object object,
                        PipelineContext context) {
        try {
            Map<String, Object> vars = new HashMap<String, Object>(1);
            vars.put( "context", context );
            MVEL.executeExpression( this.expr,
                                    object,
                                    vars );
        } catch ( Exception e ) {
            handleException( this,
                             object,
                             e );
        }
        emit( object,
              context );
    }

}
