/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.rule.builder.dialect.java;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.Dialect;
import org.drools.compiler.ReturnValueDescr;
import org.drools.process.core.ContextResolver;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.rule.builder.ReturnValueEvaluatorBuilder;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;

/**
 * @author etirelli
 *
 */
public class JavaReturnValueEvaluatorBuilder extends AbstractJavaProcessBuilder
    implements
    ReturnValueEvaluatorBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator constraintNode,
                      final ReturnValueDescr descr,
                      final ContextResolver contextResolver) {

        final String className = "returnValueEvaluator" + context.getNextId();

        JavaDialect dialect = (JavaDialect) context.getDialect( "java" );

        Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                descr,
                                                                descr.getText(),
                                                                new Set[]{Collections.EMPTY_SET, context.getPkg().getGlobals().keySet()} );

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }

        final List[] usedIdentifiers = analysis.getBoundIdentifiers();


        final Map map = createVariableContext( className,
                                               descr.getText(),
                                               (ProcessBuildContext) context,
                                               (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ),
                                               analysis.getNotBoundedIdentifiers(),
                                               contextResolver );
        map.put( "text",
                 descr.getText() );

        generatTemplates( "returnValueEvaluatorMethod",
                          "returnValueEvaluatorInvoker",
                          (ProcessBuildContext)context,
                          className,
                          map,
                          constraintNode,
                          descr );
    }

}
