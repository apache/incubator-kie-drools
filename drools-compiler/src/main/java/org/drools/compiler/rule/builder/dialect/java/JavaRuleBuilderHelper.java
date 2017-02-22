/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.java;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.spi.AcceptsClassObjectType;
import org.drools.core.spi.DeclarationScopeResolver;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.StringUtils;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class JavaRuleBuilderHelper {

    private static final Logger logger = LoggerFactory.getLogger(JavaRuleBuilderHelper.class);

    protected static TemplateRegistry RULE_REGISTRY    = new SimpleTemplateRegistry();
    protected static TemplateRegistry INVOKER_REGISTRY = new SimpleTemplateRegistry();

    protected static String JAVA_RULE_MVEL             = "javaRule.mvel";
    protected static String JAVA_INVOKERS_MVEL         = "javaInvokers.mvel";

    public static void setConsequenceTemplate( String name ) {
        JAVA_RULE_MVEL = name;
        RULE_REGISTRY = new SimpleTemplateRegistry();

    }

    public static void setInvokerTemplate( String name ) {
        JAVA_INVOKERS_MVEL = name;
        INVOKER_REGISTRY = new SimpleTemplateRegistry();
    }

    public static synchronized TemplateRegistry getRuleTemplateRegistry(ClassLoader cl) {
        if ( !RULE_REGISTRY.contains( "rules" ) ) {
            InputStream javaRuleMvelStream = JavaRuleBuilderHelper.class.getResourceAsStream( JAVA_RULE_MVEL );
            RULE_REGISTRY.addNamedTemplate( "rules",
                                            TemplateCompiler.compileTemplate( javaRuleMvelStream ) );
            try {
                javaRuleMvelStream.close();
            } catch ( IOException ex ) {
                logger.debug( "Failed to close stream!", ex );
            }
            TemplateRuntime.execute( RULE_REGISTRY.getNamedTemplate( "rules" ),
                                     null,
                                     RULE_REGISTRY );            
        }
        
        return RULE_REGISTRY;
    }

    public static synchronized TemplateRegistry getInvokerTemplateRegistry(ClassLoader cl) {
        if ( !INVOKER_REGISTRY.contains( "invokers" ) ) {
            InputStream javaInvokersMvelStream = JavaRuleBuilderHelper.class.getResourceAsStream( JAVA_INVOKERS_MVEL );
            INVOKER_REGISTRY.addNamedTemplate( "invokers",
                                               TemplateCompiler.compileTemplate( javaInvokersMvelStream ) );
            try {
                javaInvokersMvelStream.close();
            } catch ( IOException ex ) {
                logger.debug( "Failed to close stream!", ex );
            }
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

        BoundIdentifiers bindings = new BoundIdentifiers( DeclarationScopeResolver.getDeclarationClasses( decls ),
                                                          context,
                                                          Collections.EMPTY_MAP,
                                                          KnowledgeHelper.class );

        String consequenceStr = ( RuleImpl.DEFAULT_CONSEQUENCE_NAME.equals( consequenceName ) ) ?
                (String) ruleDescr.getConsequence() :
                (String) ruleDescr.getNamedConsequences().get( consequenceName );
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
        context.getRule().setRequiredDeclarationsForConsequence( consequenceName, declrStr );

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
            notPatterns[i] = (declarations[i].getExtractor() instanceof AcceptsClassObjectType) ? Boolean.FALSE : Boolean.TRUE ;
            if (indexes[i] == -1 ) {
                context.addError( new DescrBuildError( context.getParentDescr(),
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

        map.put( "className", 
                 className );
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
                    text.hashCode());
        }

        map.put( "declarations",
                 declarations );

        if ( localDeclarations != null ) {
            map.put( "localDeclarations",
                     localDeclarations );
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
        TemplateRegistry registry = getRuleTemplateRegistry(context.getKnowledgeBuilder().getRootClassLoader());

        context.addMethod((String) TemplateRuntime.execute( registry.getNamedTemplate(ruleTemplate),
                                                            null,
                                                            new MapVariableResolverFactory(vars),
                                                            registry) );
    }

    private static void generateInvokerTemplate(final String invokerTemplate,
                                                final RuleBuildContext context,
                                                final String className,
                                                final Map vars,
                                                final Object invokerLookup,
                                                final BaseDescr descrLookup) {
        TemplateRegistry registry = getInvokerTemplateRegistry(context.getKnowledgeBuilder().getRootClassLoader());
        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + StringUtils.ucFirst( className ) + "Invoker";

        context.getInvokers().put( invokerClassName,
                                   (String) TemplateRuntime.execute( registry.getNamedTemplate( invokerTemplate ),
                                                                     null,
                                                                     new MapVariableResolverFactory( vars ),
                                                                     registry ) );

        context.addInvokerLookup( invokerClassName, invokerLookup );
        context.addDescrLookups( invokerClassName, descrLookup );
    }

    public static void registerInvokerBytecode(RuleBuildContext context, Map<String, Object> vars, byte[] bytecode, Object invokerLookup) {
        String packageName = (String)vars.get("package");
        String invokerClassName = (String)vars.get("invokerClassName");
        String className = packageName + "." + invokerClassName;
        String resourceName = className.replace('.', '/') + ".class";

        JavaDialectRuntimeData data = (JavaDialectRuntimeData)context.getPkg().getDialectRuntimeRegistry().getDialectData("java");
        data.write(resourceName, bytecode);
        data.putInvoker(className, invokerLookup);
    }
}
