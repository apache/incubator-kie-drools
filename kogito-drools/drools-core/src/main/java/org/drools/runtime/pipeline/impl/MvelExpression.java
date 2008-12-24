package org.drools.runtime.pipeline.impl;

import java.io.Serializable;

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

    public void signal(Object object,
                       PipelineContext context) {
        Object result = null;
        try {
            result = MVEL.executeExpression( this.expr,
                                             object );
        } catch ( Exception e ) {
            handleException( this,
                             object,
                             e );
        }
        emit( result,
              context );
    }

}
