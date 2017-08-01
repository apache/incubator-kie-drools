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

package org.jbpm.process.builder.dialect.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;

public class JavaReturnValueEvaluatorBuilder extends AbstractJavaProcessBuilder
    implements
    ReturnValueEvaluatorBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, RuleDescr)
     */
    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator constraintNode,
                      final ReturnValueDescr descr,
                      final ContextResolver contextResolver) {

        final String className = getClassName(context);
        
        AnalysisResult analysis = getAnalysis(context, descr);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }

        buildReturnValueEvaluator(context, 
                                  constraintNode, 
                                  descr, 
                                  contextResolver, 
                                  className, 
                                  analysis);
    }
    
    protected String getClassName(PackageBuildContext context) { 
        return "returnValueEvaluator" + context.getNextId();
    }

    protected AnalysisResult getAnalysis(final PackageBuildContext context,
                                       final ReturnValueDescr descr) {
        
        JavaDialect dialect = (JavaDialect) context.getDialect( "java" );
        
        Map<String, Class<?>> variables = new HashMap<String,Class<?>>();
        BoundIdentifiers boundIdentifiers = new BoundIdentifiers(variables, context);
        AnalysisResult analysis = dialect.analyzeBlock( context,
                                                        descr,
                                                        descr.getText(),
                                                        boundIdentifiers);
        return analysis;
    }
   
    protected void buildReturnValueEvaluator(final PackageBuildContext context,
            final ReturnValueConstraintEvaluator constraintNode,
            final ReturnValueDescr descr,
            final ContextResolver contextResolver,
            String className, 
            AnalysisResult analysis) { 
        
        Set<String> identifiers = analysis.getBoundIdentifiers().getGlobals().keySet();

        final Map map = createVariableContext( className,
                descr.getText(),
                (ProcessBuildContext) context,
                (String[]) identifiers.toArray( new String[identifiers.size()] ),
                analysis.getNotBoundedIdentifiers(),
                contextResolver);
        
        map.put( "text", descr.getText() );

        generateTemplates( "returnValueEvaluatorMethod",
                "returnValueEvaluatorInvoker",
                (ProcessBuildContext)context,
                className,
                map,
                constraintNode,
                descr );
        
        collectTypes("JavaReturnValue", analysis, (ProcessBuildContext)context);
    }
}
