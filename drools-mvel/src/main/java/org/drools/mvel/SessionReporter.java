package org.drools.mvel;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.drools.kiesession.debug.StatefulKnowledgeSessionInfo;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;

public class SessionReporter {

    protected static final TemplateRegistry REPORT_REGISTRY = new SimpleTemplateRegistry();

    static {
        OptimizerFactory.setDefaultOptimizer( OptimizerFactory.SAFE_REFLECTIVE );

        REPORT_REGISTRY.addNamedTemplate( "simple",
                                          TemplateCompiler.compileTemplate( SessionReporter.class.getResourceAsStream( "reports.mvel" ),
                                                                            (Map<String, Class<? extends Node>>) null ) );

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
        Map<String, Object> context = new HashMap<>();
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
                                                                            (Map<String, Class<? extends Node>>) null ) );
        /*
         * Process these templates
         */
        TemplateRuntime.execute( REPORT_REGISTRY.getNamedTemplate( name ),
                                 null,
                                 REPORT_REGISTRY );
    }

}
