package org.drools.rule.builder.dialect.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.compiler.AnalysisResult;
import org.drools.core.util.StringUtils;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.mvel2.compiler.AbstractParser;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;

public class AbstractJavaRuleBuilder {

    protected static final TemplateRegistry RULE_REGISTRY    = new SimpleTemplateRegistry();
    protected static final TemplateRegistry INVOKER_REGISTRY = new SimpleTemplateRegistry();

    static {
        OptimizerFactory.setDefaultOptimizer( "reflective" );

        RULE_REGISTRY.addNamedTemplate( "rules",
                                        TemplateCompiler.compileTemplate( AbstractJavaRuleBuilder.class.getResourceAsStream( "javaRule.mvel" ),
                                                                          (Map<String, Class< ? extends Node>>) null ) );
        INVOKER_REGISTRY.addNamedTemplate( "invokers",
                                           TemplateCompiler.compileTemplate( AbstractJavaRuleBuilder.class.getResourceAsStream( "javaInvokers.mvel" ),
                                                                             (Map<String, Class< ? extends Node>>) null ) );

        /**
         * Process these templates
         */
        TemplateRuntime.execute( RULE_REGISTRY.getNamedTemplate( "rules" ),
                                 null,
                                 RULE_REGISTRY );
        TemplateRuntime.execute( INVOKER_REGISTRY.getNamedTemplate( "invokers" ),
                                 null,
                                 INVOKER_REGISTRY );

    }

    public static TemplateRegistry getRuleTemplateRegistry() {
        return RULE_REGISTRY;
    }

    public static TemplateRegistry getInvokerTemplateRegistry() {
        return INVOKER_REGISTRY;
    }

    public Map<String, Object> createVariableContext(final String className,
                                                     final String text,
                                                     final RuleBuildContext context,
                                                     final Declaration[] declarations,
                                                     final Declaration[] localDeclarations,
                                                     final Map<String, Class< ? >> globals,
                                                     JavaAnalysisResult analysis) {
        final Map<String, Object> map = new HashMap<String, Object>();

        map.put( "methodName",
                 className );

        map.put( "package",
                 context.getPkg().getName() );

        map.put( "ruleClassName",
                 StringUtils.ucFirst( context.getRuleDescr().getClassName() ) );

        map.put( "invokerClassName",
                 context.getRuleDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker" );

        if ( text != null ) {
            map.put( "text",
                     text );

            map.put( "hashCode",
                     new Integer( text.hashCode() ) );
        }

        final String[] declarationTypes = new String[declarations.length];
        for ( int i = 0, size = declarations.length; i < size; i++ ) {
            declarationTypes[i] = ((JavaDialect) context.getDialect()).getTypeFixer().fix( declarations[i] );
        }

        map.put( "declarations",
                 declarations );

        map.put( "declarationTypes",
                 declarationTypes );

        if ( localDeclarations != null ) {
            final String[] localDeclarationTypes = new String[localDeclarations.length];
            for ( int i = 0, size = localDeclarations.length; i < size; i++ ) {
                localDeclarationTypes[i] = ((JavaDialect) context.getDialect()).getTypeFixer().fix( localDeclarations[i] );
            }

            map.put( "localDeclarations",
                     localDeclarations );

            map.put( "localDeclarationTypes",
                     localDeclarationTypes );
        }

        String[] globalStr = new String[globals.size()];
        String[] globalTypes = new String[globals.size()];
        int i = 0;
        for ( Entry<String, Class< ? >> entry : globals.entrySet() ) {
            globalStr[i] = entry.getKey();
            globalTypes[i] = entry.getValue().getName().replace( '$',
                                                                 '.' );
            i++;
        }

        map.put( "globals",
                 globalStr );

        map.put( "globalTypes",
                 globalTypes );

        return map;
    }

    public static void generatTemplates(final String ruleTemplate,
                                        final String invokerTemplate,
                                        final RuleBuildContext context,
                                        final String className,
                                        final Map vars,
                                        final Object invokerLookup,
                                        final BaseDescr descrLookup) {
        AbstractParser.setLanguageLevel( 5 );
        TemplateRegistry registry = getRuleTemplateRegistry();

        context.getMethods().add( TemplateRuntime.execute( registry.getNamedTemplate( ruleTemplate ),
                                                           null,
                                                           new MapVariableResolverFactory( vars ),
                                                           registry )
                    );

        registry = getInvokerTemplateRegistry();
        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker";

        context.getInvokers().put( invokerClassName,
                                       TemplateRuntime.execute( registry.getNamedTemplate( invokerTemplate ),
                                                                null,
                                                                new MapVariableResolverFactory( vars ),
                                                                registry )
                    );

        context.getInvokerLookups().put( invokerClassName,
                                             invokerLookup );
        context.getDescrLookups().put( invokerClassName,
                                           descrLookup );
    }
}
