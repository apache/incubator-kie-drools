package org.drools.dataloaders.smooks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;

import org.drools.StatelessSession;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalStatelessSession;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class DroolsSmooksStatelessSession {
    private Smooks                    smooks;
    private InternalStatelessSession          session;
    private DroolsSmooksConfiguration configuration;
    private Serializable              getterExpr;

    public DroolsSmooksStatelessSession(StatelessSession session,
                                        Smooks smooks) {
        this( session,
              smooks,
              new DroolsSmooksConfiguration() );

    }

    public DroolsSmooksStatelessSession(StatelessSession session,
                                        Smooks smooks,
                                        DroolsSmooksConfiguration configuration) {
        this.smooks = smooks;
        this.configuration = configuration;
        this.session = ( InternalStatelessSession ) session;

        if ( this.configuration.getIterableGetter() != null ) {
            final ParserContext parserContext = new ParserContext();
            parserContext.setStrictTypeEnforcement( false );

            ExpressionCompiler compiler = new ExpressionCompiler( this.configuration.getIterableGetter() );
            this.getterExpr = compiler.compile( parserContext );
        }

    }

    public void executeFilter(Source source) {
        JavaResult result = new JavaResult();

        // preserve the previous classloader, While we make Smooks aware of the Drools classloader
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( ((InternalRuleBase) this.session.getRuleBase()).getRootClassLoader() );

        ExecutionContext executionContext = this.smooks.createExecutionContext();

        // Filter the input message to extract, using the execution context...
        smooks.filter( source,
                       result,
                       executionContext );

        Thread.currentThread().setContextClassLoader( previousClassLoader );


        Object object = result.getBean( this.configuration.getRoodId() );
        if ( object == null ) {
            return;
        }

        if ( this.getterExpr != null ) {
            Iterable it = (Iterable) MVEL.executeExpression( this.getterExpr,
                                                             object );
            
            if ( it != null ) {
                this.session.execute( toList( it ) );
            }
        } else {
            this.session.execute( object );

        }
    }
    
    private List toList( Iterable it) {
        List list = new ArrayList();
        for ( Object o : it ) {
            list.add( o );
        }
        
        return list;
    }
}
