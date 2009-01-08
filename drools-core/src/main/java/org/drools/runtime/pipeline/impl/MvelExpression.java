package org.drools.runtime.pipeline.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.pipeline.Expression;
import org.drools.runtime.pipeline.PipelineContext;
import org.drools.runtime.pipeline.Receiver;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class MvelExpression extends BaseEmitter
    implements
    Expression,
    Receiver {
    private Serializable expr;

    public MvelExpression(String text) {
        final ParserContext parserContext = new ParserContext();
        parserContext.setStrictTypeEnforcement( false );

        ExpressionCompiler compiler = new ExpressionCompiler( text );
        this.expr = compiler.compile( parserContext );
    }

    public void receive(Object object,
                       PipelineContext context) {
        Object result = null;
        try {
            Map<String, Object> vars = new HashMap<String, Object>(1);
            vars.put( "context", context );
            result = MVEL.executeExpression( this.expr,
                                    object,
                                    vars );
        } catch ( Exception e ) {
            handleException( this,
                             object,
                             e );
        }
        emit( result,
              context );
    }

}
