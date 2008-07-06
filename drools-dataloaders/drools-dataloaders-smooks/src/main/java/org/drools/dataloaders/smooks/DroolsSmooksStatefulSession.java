package org.drools.dataloaders.smooks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import org.drools.FactHandle;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.compiler.ExpressionCompiler;

public class DroolsSmooksStatefulSession {
    private Smooks                    smooks;
    private StatefulSession           session;
    private DroolsSmooksConfiguration configuration;
    private Serializable              getterExpr;

    public DroolsSmooksStatefulSession(StatefulSession session,
                                       Smooks smooks) {
        this( session,
              smooks,
              new DroolsSmooksConfiguration() );

    }

    public DroolsSmooksStatefulSession(StatefulSession session,
                                       Smooks smooks,
                                       DroolsSmooksConfiguration configuration) {
        this.smooks = smooks;
        this.configuration = configuration;
        this.session = session;

        if ( this.configuration.getIterableGetter() != null ) {
            final ParserContext parserContext = new ParserContext();
            parserContext.setStrictTypeEnforcement( false );

            ExpressionCompiler compiler = new ExpressionCompiler( this.configuration.getIterableGetter() );
            this.getterExpr = compiler.compile( parserContext );
        }

    }

    public Map insertFilter(Source source) {
        JavaResult result = new JavaResult();

        // preserve the previous classloader, While we make Smooks aware of the Drools classloader
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( ((InternalRuleBase) this.session.getRuleBase()).getCompositePackageClassLoader() );

        ExecutionContext executionContext = this.smooks.createExecutionContext();

        // Filter the input message to extract, using the execution context...
        smooks.filter( source,
                       result,
                       executionContext );

        Thread.currentThread().setContextClassLoader( previousClassLoader );

        Map handles = new HashMap<FactHandle, Object>();

        Object object = result.getBean( this.configuration.getRoodId() );
        if ( object == null ) {
            return handles;
        }

        if ( this.getterExpr != null ) {
            Iterable it = (Iterable) MVEL.executeExpression( this.getterExpr,
                                                             object );
            if ( it != null ) {
                for ( Object item : it ) {
                    FactHandle handle = this.session.insert( item );
                    handles.put( handle,
                                 object );
                }
            }
        } else {
            FactHandle handle = this.session.insert( object );
            handles.put( handle,
                         object );

        }

        return handles;
    }
}
