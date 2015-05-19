package org.jbpm.kie.services.impl.bpmn2.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.jbpm.kie.services.impl.bpmn2.ProcessDescRepoHelper;
import org.jbpm.kie.services.impl.bpmn2.builder.DataServiceExpressionBuilder;
import org.jbpm.process.builder.dialect.mvel.MVELActionBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;

class DataServiceMvelActionBuilder extends MVELActionBuilder implements DataServiceExpressionBuilder {

    private static final ThreadLocal<ProcessDescRepoHelper> threadLocalHelper 
        = new ThreadLocal<ProcessDescRepoHelper>();

    @Override
    public void setProcessHelperForThread( ProcessDescRepoHelper helper ) {
       threadLocalHelper.set(helper);
    }

    @Override
    public ProcessDescRepoHelper getProcessHelperForThread() {
       return threadLocalHelper.get();
    }
    
    @Override
    public void build( PackageBuildContext context, 
                       DroolsAction action, 
                       ActionDescr actionDescr, 
                       ContextResolver contextResolver ) {
       
        String text = processMacros( actionDescr.getText() );
        Map<String, Class<?>> variables = new HashMap<String,Class<?>>();

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            AnalysisResult analysis = getAnalysis(context, actionDescr, dialect, text, variables);

            MVELAnalysisResult mvelAnalysis = (MVELAnalysisResult) analysis;
            ProcessDescRepoHelper helper = getProcessHelperForThread();
            for( Class<?> varClass : mvelAnalysis.getMvelVariables().values() ) { 
                helper.getReferencedClasses().add(varClass.getCanonicalName());
            }

            // if this should actually compile the expression, see the parent class build(..) method for what should go here
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                     actionDescr,
                                     null,
                                     "Unable to build expression for action '" + actionDescr.getText() + "' :" + e ) );
        }
    }
  
}
