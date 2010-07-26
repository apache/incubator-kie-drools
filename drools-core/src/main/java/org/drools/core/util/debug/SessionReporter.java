/**
 * Copyright 2010 JBoss Inc
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

package org.drools.core.util.debug;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.mvel2.compiler.AbstractParser;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class SessionReporter {

    protected static final TemplateRegistry REPORT_REGISTRY = new SimpleTemplateRegistry();

    static {
        OptimizerFactory.setDefaultOptimizer( "reflective" );

        REPORT_REGISTRY.addNamedTemplate( "simple",
                                          TemplateCompiler.compileTemplate( SessionReporter.class.getResourceAsStream( "reports.mvel" ),
                                                                            null ) );

        /**
         * Process these templates
         */
        TemplateRuntime.execute( REPORT_REGISTRY.getNamedTemplate( "simple" ),
                                 null,
                                 REPORT_REGISTRY );
    }

    public static String generateReport(final String ruleTemplate,
                                        final StatefulKnowledgeSessionInfo session,
                                        final Map<String, Object> vars) {
        AbstractParser.setLanguageLevel( 5 );

        Map<String, Object> context = new HashMap<String, Object>();
        if ( vars != null ) {
            context.putAll( vars );
        }
        context.put( "session",
                     session );

        return (String) TemplateRuntime.execute( REPORT_REGISTRY.getNamedTemplate( ruleTemplate ),
                                                 null,
                                                 new MapVariableResolverFactory( context ),
                                                 REPORT_REGISTRY );
    }

    public static void addNamedTemplate(String name,
                                        InputStream template) {
        REPORT_REGISTRY.addNamedTemplate( name,
                                          TemplateCompiler.compileTemplate( template,
                                                                            null ) );
        /*
         * Process these templates
         */
        TemplateRuntime.execute( REPORT_REGISTRY.getNamedTemplate( name ),
                                 null,
                                 REPORT_REGISTRY );
    }

}
