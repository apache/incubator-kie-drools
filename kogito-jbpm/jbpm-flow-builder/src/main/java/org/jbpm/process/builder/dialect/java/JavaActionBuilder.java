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

package org.jbpm.process.builder.dialect.java;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.ActionDescr;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.workflow.core.DroolsAction;

/**
 * @author etirelli
 *
 */
public class JavaActionBuilder extends AbstractJavaProcessBuilder
    implements
    ActionBuilder {

    public void build(final PackageBuildContext context,
                      final DroolsAction action,
                      final ActionDescr actionDescr,
                      final ContextResolver contextResolver) {

        final String className = "action" + context.getNextId();               

        JavaDialect dialect = (JavaDialect) context.getDialect( "java" );
        
        Map<String, Class<?>> variables = new HashMap<String,Class<?>>();
        BoundIdentifiers boundIdentifiers = new BoundIdentifiers(variables, context.getPackageBuilder().getGlobals());
        AnalysisResult analysis = dialect.analyzeBlock( context,
                                                        actionDescr,
                                                        actionDescr.getText(),
                                                        boundIdentifiers);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return;
        }

        Set<String> identifiers = analysis.getBoundIdentifiers().getGlobals().keySet();

        final Map map = createVariableContext( className,
                                               actionDescr.getText(),
                                               (ProcessBuildContext) context,
                                               (String[]) identifiers.toArray( new String[identifiers.size()] ),
                                               analysis.getNotBoundedIdentifiers(),
                                               contextResolver);
        map.put( "text",
                 ProcessKnowledgeHelperFixer.fix( actionDescr.getText() ));

        generatTemplates( "actionMethod",
                          "actionInvoker",
                          (ProcessBuildContext)context,
                          className,
                          map,
                          action,
                          actionDescr );
    }

}
