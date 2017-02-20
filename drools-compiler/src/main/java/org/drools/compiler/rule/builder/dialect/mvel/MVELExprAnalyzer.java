/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder.dialect.mvel;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.RuleConditionElement;
import org.kie.api.definition.rule.Rule;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.optimizers.OptimizerFactory;
import org.mvel2.util.PropertyTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Expression analyzer.
 */
public class MVELExprAnalyzer {

    private static final Logger log = LoggerFactory.getLogger( MVELExprAnalyzer.class );

    static {
        // always use mvel reflective optimizer
        OptimizerFactory.setDefaultOptimizer(OptimizerFactory.SAFE_REFLECTIVE);
    }

    public MVELExprAnalyzer() {
        // intentionally left blank.
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Analyze an expression.
     * 
     * @param expr
     *            The expression to analyze.
     * @param availableIdentifiers
     *            Total set of declarations available.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     * @throws RecognitionException 
     *             If an error occurs in the parser.
     */
    @SuppressWarnings("unchecked")
    public static MVELAnalysisResult analyzeExpression(final PackageBuildContext context,
                                                       final String expr,
                                                       final BoundIdentifiers availableIdentifiers,
                                                       final Map<String, Class< ? >> localTypes,
                                                       String contextIdentifier,
                                                       Class kcontextClass) {
        if ( expr.trim().length() <= 0 ) {
            MVELAnalysisResult result = analyze( (Set<String>) Collections.EMPTY_SET, availableIdentifiers );
            result.setMvelVariables( new HashMap<String, Class< ? >>() );
            result.setTypesafe( true );
            return result;
        }

        MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
        MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
        MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
        MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;

        MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

        ParserConfiguration conf = context.getMVELDialectRuntimeData().getParserConfiguration();

        conf.setClassLoader( context.getKnowledgeBuilder().getRootClassLoader() );

        // first compilation is for verification only
        // @todo proper source file name
        final ParserContext parserContext1 = new ParserContext( conf );
        if ( localTypes != null ) {
            for ( Entry entry : localTypes.entrySet() ) {
                parserContext1.addInput( (String) entry.getKey(),
                                         (Class) entry.getValue() );
            }
        }
        if ( availableIdentifiers.getThisClass() != null ) {
            parserContext1.addInput( "this",
                                     availableIdentifiers.getThisClass() );
        }

        if ( availableIdentifiers.getOperators() != null ) {
            for ( Entry<String, EvaluatorWrapper> opEntry : availableIdentifiers.getOperators().entrySet() ) {
                parserContext1.addInput( opEntry.getKey(), opEntry.getValue().getClass() );
            }
        }

        parserContext1.setStrictTypeEnforcement( false );
        parserContext1.setStrongTyping( false );
        parserContext1.setInterceptors( dialect.getInterceptors() );
        Class< ? > returnType;

        try {
            returnType = MVEL.analyze( expr,
                                       parserContext1 );
        } catch ( Exception e ) {
            BaseDescr base = (context instanceof RuleBuildContext) ? ((RuleBuildContext)context).getRuleDescr() : context.getParentDescr();
            DialectUtil.copyErrorLocation(e, context.getParentDescr());
            context.addError( new DescrBuildError( base,
                                                   context.getParentDescr(),
                                                   null,
                                                   "Unable to Analyse Expression " + expr + ":\n" + e.getMessage() ) );
            return null;
        }

        Set<String> requiredInputs = new HashSet<String>();
        requiredInputs.addAll( parserContext1.getInputs().keySet() );
        HashMap<String, Class< ? >> variables = (HashMap<String, Class< ? >>) ((Map) parserContext1.getVariables());
        if ( localTypes != null ) {
            for ( String str : localTypes.keySet() ) {
                // we have to do this due to mvel regressions on detecting true local vars
                variables.remove( str );
            }
        }

        // MVEL includes direct fields of context object in non-strict mode. so we need to strip those
        if ( availableIdentifiers.getThisClass() != null ) {
            requiredInputs.removeIf( s -> PropertyTools.getFieldOrAccessor( availableIdentifiers.getThisClass(), s ) != null );
        }

        // now, set the required input types and compile again
        final ParserContext parserContext2 = new ParserContext( conf );
        parserContext2.setStrictTypeEnforcement( true );
        parserContext2.setStrongTyping( true );
        parserContext2.setInterceptors( dialect.getInterceptors() );

        for ( String input : requiredInputs ) {
            if ("this".equals( input )) {
                continue;
            }

            Class< ? > cls = availableIdentifiers.resolveType( input );
            if ( cls == null ) {
                if ( input.equals( contextIdentifier ) || input.equals( "kcontext" ) ) {
                    cls = kcontextClass;
                } else if ( input.equals( "rule" ) ) {
                    cls = Rule.class;
                } else if ( localTypes != null ) {
                    cls = localTypes.get( input );
                }
            }
            if ( cls != null ) {
                parserContext2.addInput( input, cls );
            }
        }

        if ( availableIdentifiers.getThisClass() != null ) {
            parserContext2.addInput( "this", availableIdentifiers.getThisClass() );
        }

        boolean typesafe = context.isTypesafe();

        try {
            returnType = MVEL.analyze( expr,
                                       parserContext2 );
            typesafe = true;
        } catch ( Exception e ) {
            // is this an error, or can we fall back to non-typesafe mode?
            if ( typesafe ) {
                BaseDescr base = (context instanceof RuleBuildContext) ? ((RuleBuildContext)context).getRuleDescr() : context.getParentDescr();
                DialectUtil.copyErrorLocation(e, context.getParentDescr());
                context.addError( new DescrBuildError( base,
                                                       context.getParentDescr(),
                                                       null,
                                                       "Unable to Analyse Expression " + expr + ":\n" + e.getMessage() ) );
                return null;
            }
        }

        if ( typesafe ) {
            requiredInputs = new HashSet<String>();
            requiredInputs.addAll( parserContext2.getInputs().keySet() );
            requiredInputs.addAll( variables.keySet() );
            variables = (HashMap<String, Class< ? >>) ((Map) parserContext2.getVariables());
            if ( localTypes != null ) {
                for ( String str : localTypes.keySet() ) {
                    // we have to do this due to mvel regressions on detecting true local vars
                    variables.remove( str );
                }
            }
        }

        MVELAnalysisResult result = analyze( requiredInputs, availableIdentifiers );
        result.setReturnType( returnType );
        result.setMvelVariables( variables );
        result.setTypesafe( typesafe );
        return result;
    }

    /**
     * Analyse an expression.
     * @throws RecognitionException
     *             If an error occurs in the parser.
     */
    private static MVELAnalysisResult analyze(final Set<String> identifiers,
                                              final BoundIdentifiers availableIdentifiers) {

        MVELAnalysisResult result = new MVELAnalysisResult();
        result.setIdentifiers( identifiers );

        final Set<String> notBound = new HashSet<String>( identifiers );
        notBound.remove( "this" );
        Map<String, Class< ? >> usedDecls = new HashMap<String, Class< ? >>();
        Map<String, Class< ? >> usedGlobals = new HashMap<String, Class< ? >>();
        Map<String, EvaluatorWrapper> usedOperators = new HashMap<String, EvaluatorWrapper>();

        for ( Entry<String, Class< ? >> entry : availableIdentifiers.getDeclrClasses().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedDecls.put( entry.getKey(),
                               entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        for ( String identifier : identifiers ) {
            Class<?> type = availableIdentifiers.resolveVarType( identifier );
            if (type != null) {
                usedGlobals.put( identifier, type );
                notBound.remove( identifier );
            }
        }

        for ( Map.Entry<String, EvaluatorWrapper> op : availableIdentifiers.getOperators().entrySet() ) {
            if ( identifiers.contains( op.getKey() ) ) {
                usedOperators.put( op.getKey(),
                                   op.getValue() );
                notBound.remove( op.getKey() );
            }
        }

        BoundIdentifiers boundIdentifiers = new BoundIdentifiers( usedDecls,
                                                                  availableIdentifiers.getContext(),
                                                                  usedOperators,
                                                                  availableIdentifiers.getThisClass() );
        boundIdentifiers.setGlobals( usedGlobals );

        result.setBoundIdentifiers( boundIdentifiers );
        result.setNotBoundedIdentifiers( notBound );

        return result;
    }

    public static Class<?> getExpressionType(PackageBuildContext context,
                                             Map<String, Class< ? >> declCls,
                                             RuleConditionElement source,
                                             String expression) {
        MVELDialectRuntimeData data = ( MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
        ParserConfiguration conf = data.getParserConfiguration();
        conf.setClassLoader( context.getKnowledgeBuilder().getRootClassLoader() );
        ParserContext pctx = new ParserContext( conf );
        pctx.setStrongTyping(true);
        pctx.setStrictTypeEnforcement(true);
        for (Map.Entry<String, Class< ? >> entry : declCls.entrySet()) {
            pctx.addInput(entry.getKey(), entry.getValue());
        }
        for (Declaration decl : source.getOuterDeclarations().values()) {
            pctx.addInput(decl.getBindingName(), decl.getDeclarationClass());
        }
        try {
            return MVEL.analyze( expression, pctx );
        } catch (Exception e) {
            log.warn( "Unable to parse expression: " + expression, e );
        }
        return null;
    }
}
