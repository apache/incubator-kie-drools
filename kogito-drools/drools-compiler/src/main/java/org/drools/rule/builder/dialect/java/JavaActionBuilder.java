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
import org.drools.lang.descr.ActionDescr;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.rule.builder.ActionBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.workflow.core.node.ActionNode;

/**
 * @author etirelli
 *
 */
public class JavaActionBuilder extends AbstractJavaProcessBuilder
    implements
    ActionBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final PackageBuildContext context,
                      final ActionNode actionNode,
                      final ActionDescr actionDescr) {
        build( context,
               actionNode,
               actionDescr,
               null );
    }
    public void build(final PackageBuildContext context,
                      final ActionNode actionNode,
                      final ActionDescr actionDescr,
                      final VariableScope variableScope) {

        final String className = "action" + context.getNextId();               

        JavaDialect dialect = (JavaDialect) context.getDialect( "java" );

        Dialect.AnalysisResult analysis = dialect.analyzeBlock( context,
                                                                actionDescr,
                                                                actionDescr.getText(),
                                                                new Set[]{Collections.EMPTY_SET, context.getPkg().getGlobals().keySet()} );

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }

        final List[] usedIdentifiers = analysis.getBoundIdentifiers();


        final Map map = createVariableContext( className,
                                               actionDescr.getText(),
                                               (ProcessBuildContext) context,
                                               (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );
        map.put( "text",
                 dialect.getKnowledgeHelperFixer().fix( actionDescr.getText() ));

        generatTemplates( "actionMethod",
                          "actionInvoker",
                          (ProcessBuildContext)context,
                          className,
                          map,
                          actionNode,
                          actionDescr );
    }

}
