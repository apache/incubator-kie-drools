package org.drools.rule.builder.dialect.java;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.util.StringUtils;
import org.mvel.MVEL;
import org.mvel.MVELTemplateRegistry;
import org.mvel.TemplateInterpreter;
import org.mvel.TemplateRegistry;

public class AbstractJavaProcessBuilder {

    protected static final TemplateRegistry RULE_REGISTRY    = new MVELTemplateRegistry();
    protected static final TemplateRegistry INVOKER_REGISTRY = new MVELTemplateRegistry();

    static {
        MVEL.setThreadSafe( true );        
        RULE_REGISTRY.registerTemplate( new InputStreamReader( AbstractJavaProcessBuilder.class.getResourceAsStream( "javaRule.mvel" ) ) );
        INVOKER_REGISTRY.registerTemplate( new InputStreamReader( AbstractJavaProcessBuilder.class.getResourceAsStream( "javaInvokers.mvel" ) ) );

    }

    public TemplateRegistry getRuleTemplateRegistry() {
        return RULE_REGISTRY;
    }

    public TemplateRegistry getInvokerTemplateRegistry() {
        return INVOKER_REGISTRY;
    }

    public Map createVariableContext(final String className,
                                     final String text,
                                     final ProcessBuildContext context,
                                     final String[] globals) {
        final Map map = new HashMap();

        map.put( "methodName",
                 className );

        map.put( "package",
                 context.getPkg().getName() );

        map.put( "processClassName",
                 StringUtils.ucFirst( context.getProcessDescr().getClassName() ) );

        map.put( "invokerClassName",
                 context.getProcessDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker" );

        if ( text != null ) {
            map.put( "text",
                     text );

            map.put( "hashCode",
                     new Integer( text.hashCode() ) );
        }

        final List globalTypes = new ArrayList( globals.length );
        for ( int i = 0, length = globals.length; i < length; i++ ) {
            globalTypes.add( ((Class) context.getPkg().getGlobals().get( globals[i] )).getName().replace( '$',
                                                                                                          '.' ) );
        }

        map.put( "globals",
                 globals );

        map.put( "globalTypes",
                 globalTypes );

        return map;
    }

    public void generatTemplates(final String ruleTemplate,
                                 final String invokerTemplate,
                                 final ProcessBuildContext context,
                                 final String className,
                                 final Map vars,
                                 final Object invokerLookup,
                                 final BaseDescr descrLookup) {
        TemplateRegistry registry = getRuleTemplateRegistry();
        context.getMethods().add( TemplateInterpreter.parse( registry.getTemplate( ruleTemplate ),
                                                             null,
                                                             vars,
                                                             registry ) );

        registry = getInvokerTemplateRegistry();
        final String invokerClassName = context.getPkg().getName() + "." + context.getProcessDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker";
        context.getInvokers().put( invokerClassName,
                                   TemplateInterpreter.parse( registry.getTemplate( invokerTemplate ),
                                                              null,
                                                              vars,
                                                              registry ) );

        context.getInvokerLookups().put( invokerClassName,
                                         invokerLookup );
        context.getDescrLookups().put( invokerClassName,
                                       descrLookup );
    }
}
