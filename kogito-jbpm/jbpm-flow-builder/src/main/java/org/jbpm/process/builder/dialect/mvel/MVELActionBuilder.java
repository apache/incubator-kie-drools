/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.builder.dialect.mvel;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.MVELAction;
import org.jbpm.workflow.core.DroolsAction;
import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MVELActionBuilder extends AbstractMVELBuilder implements ActionBuilder {

    private static final Map<String, Macro> macros = new HashMap<String, Macro>( 5 );
    static {
        macros.put( "insert",
                    new Macro() {
                        public String doMacro() {
                            return "kcontext.getKnowledgeRuntime().insert";
                        }
                    } );

//        macros.put( "insertLogical",
//                    new Macro() {
//                        public String doMacro() {
//                            return "kcontext.getKnowledgeRuntime()..insertLogical";
//                        }
//                    } );


//        macros.put( "update",
//                    new Macro() {
//                        public String doMacro() {
//                            return "kcontext.getKnowledgeRuntime().update";
//                        }
//                    } );

//        macros.put( "retract",
//                    new Macro() {
//                        public String doMacro() {
//                            return "kcontext.getKnowledgeRuntime().retract";
//                        }
//                    } );;
    }
    
    public MVELActionBuilder() {

    }

    public static String processMacros(String consequence) {
        MacroProcessor macroProcessor = new MacroProcessor();
        macroProcessor.setMacros( macros );
        return macroProcessor.parse( delimitExpressions( consequence ) );
    }

 
    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr,
                      final ContextResolver contextResolver) {

        String text = processMacros( actionDescr.getText() );
        Map<String, Class<?>> variables = new HashMap<String,Class<?>>();
        
        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            MVELAnalysisResult analysis = getAnalysis(context, actionDescr, dialect, text, variables);

            if ( analysis == null ) {
                // not possible to get the analysis results
                return;
            }
            
            buildAction(context,
                        action,
                        actionDescr,
                        contextResolver,
                        dialect, 
                        analysis,
                        text,
                        variables);
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                     actionDescr,
                                     null,
                                     "Unable to build expression for action '" + actionDescr.getText() + "' :" + e ) );
        }
    }

 
    protected void buildAction(final PackageBuildContext context,
            final DroolsAction action,
            final ActionDescr actionDescr,
            final ContextResolver contextResolver, 
            final MVELDialect dialect,
            final MVELAnalysisResult analysis, 
            final String text, 
            Map<String, Class<?>> variables) throws Exception {
       
        Set<String> variableNames = analysis.getNotBoundedIdentifiers();
        if (contextResolver != null) {
            for (String variableName: variableNames) {
                if ( analysis.getMvelVariables().keySet().contains( variableName ) 
                     ||  variableName.equals( "kcontext" ) 
                     || variableName.equals( "context" ) ) {
                    continue;
                }
                VariableScope variableScope 
                    = (VariableScope) contextResolver.resolveContext(VariableScope.VARIABLE_SCOPE, variableName);
                if (variableScope == null) {
                    context.getErrors().add(
                            new DescrBuildError(context.getParentDescr(),
                                                actionDescr,
                                                null,
                                                "Could not find variable '" + variableName + "' "
                                                 + "for action '" + actionDescr.getText() + "'" ) );
                } else {
                    variables.put(variableName,
                            context.getDialect().getTypeResolver().resolveType(
                                    variableScope.findVariable(variableName).getType().getStringType()));
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
                                                                   org.kie.api.runtime.process.ProcessContext.class,
                                                                   false,
                                                                   MVELCompilationUnit.Scope.EXPRESSION);
        MVELAction expr = new MVELAction( unit, context.getDialect().getId() );
        action.setMetaData("Action",  expr );

        MVELDialectRuntimeData data 
            = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( dialect.getId() );            
        data.addCompileable( action, expr );  

        expr.compile( data );
        
        collectTypes("MVELDialect", analysis, (ProcessBuildContext) context);
    }
    
}
