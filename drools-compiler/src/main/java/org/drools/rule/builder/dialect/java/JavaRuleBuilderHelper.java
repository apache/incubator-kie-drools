package org.drools.rule.builder.dialect.java;

import org.drools.compiler.*;
import org.drools.core.util.*;
import org.drools.lang.descr.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.*;
import org.mvel2.integration.impl.*;
import org.mvel2.templates.*;

import java.util.*;
import java.util.Map.Entry;

public final class JavaRuleBuilderHelper {

    protected static final TemplateRegistry RULE_REGISTRY    = new SimpleTemplateRegistry();
    protected static final TemplateRegistry INVOKER_REGISTRY = new SimpleTemplateRegistry();

    public static synchronized TemplateRegistry getRuleTemplateRegistry(ClassLoader cl) {
        if ( !RULE_REGISTRY.contains( "rules" ) ) {
            ParserConfiguration pconf = new ParserConfiguration();
            pconf.setClassLoader( cl );
            
            ParserContext pctx = new ParserContext(pconf);
            RULE_REGISTRY.addNamedTemplate( "rules",
                                            TemplateCompiler.compileTemplate( JavaRuleBuilderHelper.class.getResourceAsStream( "javaRule.mvel" ),
                                                                              pctx ) );
            TemplateRuntime.execute( RULE_REGISTRY.getNamedTemplate( "rules" ),
                                     null,
                                     RULE_REGISTRY );            
        }
        
        return RULE_REGISTRY;
    }

    public static synchronized TemplateRegistry getInvokerTemplateRegistry(ClassLoader cl) {
        if ( !INVOKER_REGISTRY.contains( "invokers" ) ) {
            ParserConfiguration pconf = new ParserConfiguration();
            pconf.setClassLoader( cl );
            
            ParserContext pctx = new ParserContext(pconf);            
            INVOKER_REGISTRY.addNamedTemplate( "invokers",
                                               TemplateCompiler.compileTemplate( JavaRuleBuilderHelper.class.getResourceAsStream( "javaInvokers.mvel" ),
                                                                                 pctx ) );
            TemplateRuntime.execute( INVOKER_REGISTRY.getNamedTemplate( "invokers" ),
                                     null,
                                     INVOKER_REGISTRY );            
        }        
        return INVOKER_REGISTRY;
    }

    public static JavaAnalysisResult createJavaAnalysisResult(final RuleBuildContext context,
                                                               String consequenceName,
                                                               Map<String, Declaration> decls) {
        final RuleDescr ruleDescr = context.getRuleDescr();

        BoundIdentifiers bindings = new BoundIdentifiers(context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                         context.getPackageBuilder().getGlobals(),
                                                         null,
                                                         KnowledgeHelper.class );

        String consequenceStr = ( "default".equals( consequenceName ) ) ? (String) ruleDescr.getConsequence() : (String) ruleDescr.getNamedConsequences().get( consequenceName );
        consequenceStr = consequenceStr + "\n";

        return ( JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                         ruleDescr,
                                                                         consequenceStr,
                                                                         bindings );
    }

    public static Map<String, Object> createConsequenceContext(final RuleBuildContext context,
                                                               String consequenceName,
                                                               String className,
                                                               String consequenceText,
                                                               Map<String, Declaration> decls,
                                                               final BoundIdentifiers usedIdentifiers) {

        final Declaration[] declarations =  new Declaration[usedIdentifiers.getDeclrClasses().size()];
        String[] declrStr = new String[declarations.length];
        int j = 0;
        for (String str : usedIdentifiers.getDeclrClasses().keySet() ) {
            declrStr[j] = str;
            declarations[j++] = decls.get( str );
        }
        Arrays.sort( declarations, RuleTerminalNode.SortDeclarations.instance  );
        for ( int i = 0; i < declrStr.length; i++) {
            declrStr[i] = declarations[i].getIdentifier();
        }
        context.getRule().setRequiredDeclarations( declrStr );

        final Map<String, Object> map = createVariableContext( className,
                                                               consequenceText,
                                                               context,
                                                               declarations,
                                                               null,
                                                               usedIdentifiers.getGlobals()
        );

        map.put( "consequenceName", consequenceName );

        //final int[] indexes = new int[declarations.length];
        final Integer[] indexes = new Integer[declarations.length];

        final Boolean[] notPatterns = new Boolean[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = i;
            notPatterns[i] = (declarations[i].getExtractor() instanceof PatternExtractor) ? Boolean.FALSE : Boolean.TRUE ;
            if ( (indexes[i]).intValue() == -1 ) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              context.getRuleDescr(),
                                                              null,
                                                              "Internal Error : Unable to find declaration in list while generating the consequence invoker" ) );
            }
        }

        map.put( "indexes",
                 indexes );

        map.put( "notPatterns",
                 notPatterns );

        return map;
    }

    public static Map<String, Object> createVariableContext(final String className,
                                                     final String text,
                                                     final RuleBuildContext context,
                                                     final Declaration[] declarations,
                                                     final Declaration[] localDeclarations,
                                                     final Map<String, Class<?>> globals) {
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
            if ( declarationTypes[i] == null ) {
                // declaration type was not fixed properly, assume this was reported from it's original problem
                //return null;
                declarationTypes[i] = "java.lang.Object";
            }
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

    public static void generateTemplates(final String ruleTemplate,
                                         final String invokerTemplate,
                                         final RuleBuildContext context,
                                         final String className,
                                         final Map vars,
                                         final Object invokerLookup,
                                         final BaseDescr descrLookup) {

        generateMethodTemplate(ruleTemplate, context, vars);
        generateInvokerTemplate(invokerTemplate, context, className, vars, invokerLookup, descrLookup);
    }

    public static void generateMethodTemplate(final String ruleTemplate, final RuleBuildContext context, final Map vars) {
        TemplateRegistry registry = getRuleTemplateRegistry(context.getPackageBuilder().getRootClassLoader());

        context.getMethods().add( TemplateRuntime.execute( registry.getNamedTemplate( ruleTemplate ),
                                                           null,
                                                           new MapVariableResolverFactory( vars ),
                                                           registry ) );
    }

    public static void generateInvokerTemplate(final String invokerTemplate,
                                               final RuleBuildContext context,
                                               final String className,
                                               final Map vars,
                                               final Object invokerLookup,
                                               final BaseDescr descrLookup) {
        TemplateRegistry registry = getInvokerTemplateRegistry(context.getPackageBuilder().getRootClassLoader());
        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker";

        context.getInvokers().put( invokerClassName,
                                   TemplateRuntime.execute( registry.getNamedTemplate( invokerTemplate ),
                                                            null,
                                                            new MapVariableResolverFactory( vars ),
                                                            registry ) );

        context.getInvokerLookups().put( invokerClassName,
                                             invokerLookup );
        context.getDescrLookups().put( invokerClassName,
                                           descrLookup );
    }
}
