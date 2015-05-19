package org.jbpm.process.builder.dialect.mvel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.MVELReturnValueEvaluator;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;

public class MVELReturnValueEvaluatorBuilder extends AbstractMVELBuilder 
    implements
    ReturnValueEvaluatorBuilder {

    public MVELReturnValueEvaluatorBuilder() {

    }

    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator constraintNode,
                      final ReturnValueDescr descr,
                      final ContextResolver contextResolver) {

        String text = descr.getText();
        Map<String, Class<?>> variables = new HashMap<String,Class<?>>();

        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            MVELAnalysisResult analysis = getAnalysis(context, descr, dialect, text, variables);

            if ( analysis == null ) {
                // not possible to get the analysis results
                return;
            }

            buildReturnValueEvaluator(context, 
                                      constraintNode, 
                                      descr, 
                                      contextResolver, 
                                      dialect, 
                                      analysis, 
                                      text, 
                                      variables);
          
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          descr,
                                                          null,
                                                          "Unable to build expression for 'constraint' " + descr.getText() + "': " + e ) );
        }
    }

    public void buildReturnValueEvaluator(final PackageBuildContext context,
            final ReturnValueConstraintEvaluator constraintNode,
            final ReturnValueDescr descr,
            final ContextResolver contextResolver, 
            final MVELDialect dialect,
            final MVELAnalysisResult analysis, 
            final String text, 
            Map<String, Class<?>> variables) throws Exception {
        
        Set<String> variableNames = analysis.getNotBoundedIdentifiers();
        if (contextResolver != null) {
            for (String variableName: variableNames) {
                if (  analysis.getMvelVariables().keySet().contains( variableName ) ||  variableName.equals( "kcontext" ) || variableName.equals( "context" ) ) {
                    continue;
                }                    
                VariableScope variableScope = (VariableScope) contextResolver.resolveContext(VariableScope.VARIABLE_SCOPE, variableName);
                if (variableScope == null) {
                    context.getErrors().add(
                        new DescrBuildError(
                            context.getParentDescr(),
                            descr,
                            null,
                            "Could not find variable '" + variableName + "' for action '" + descr.getText() + "'" ) );                    
                } else {
                    variables.put(variableName,
                                  context.getDialect().getTypeResolver().resolveType(variableScope.findVariable(variableName).getType().getStringType()));
                }
            }
        }

        MVELCompilationUnit unit = dialect.getMVELCompilationUnit( text,
                                                                   analysis,
                                                                   null,
                                                                   null,
                                                                   variables,
                                                                   context,
                                                                   "context",
                                                                   org.kie.api.runtime.process.ProcessContext.class);
        // MVELReturnValueExpression expr = new MVELReturnValueExpression( unit, context.getDialect().getId() );

        MVELReturnValueEvaluator expr = new MVELReturnValueEvaluator( unit,
                                                                      dialect.getId() );
        // expr.setVariableNames(variableNames);

        constraintNode.setEvaluator( expr );
        
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( dialect.getId() );
        data.addCompileable( constraintNode,
                              expr );
        
        expr.compile( data );
    }
}
