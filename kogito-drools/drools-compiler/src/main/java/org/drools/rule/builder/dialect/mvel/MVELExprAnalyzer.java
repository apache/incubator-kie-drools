/*
 * Copyright 2005 JBoss Inc
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

package org.drools.rule.builder.dialect.mvel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.drools.base.EvaluatorWrapper;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.definition.rule.Rule;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.builder.DroolsCompilerComponentFactory;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.util.PropertyTools;

import static org.drools.rule.builder.dialect.DialectUtil.copyErrorLocation;

/**
 * Expression analyzer.
 */
public class MVELExprAnalyzer {

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
    public MVELAnalysisResult analyzeExpression(final PackageBuildContext context,
                                                final String expr,
                                                final BoundIdentifiers availableIdentifiers,
                                                final Map<String, Class< ? >> localTypes,
                                                String contextIndeifier,
                                                Class kcontextClass) {
        MVELAnalysisResult result;
        if ( expr.trim().length() > 0 ) {
            MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
            MVEL.COMPILER_OPT_ALLOW_OVERRIDE_ALL_PROPHANDLING = true;
            MVEL.COMPILER_OPT_ALLOW_RESOLVE_INNERCLASSES_WITH_DOTNOTATION = true;
            MVEL.COMPILER_OPT_SUPPORT_JAVA_STYLE_CLASS_LITERALS = true;   
            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );
            
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            ParserConfiguration conf = data.getParserConfiguration();

            conf.setClassLoader( context.getPackageBuilder().getRootClassLoader() );

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
                for ( String opKey : availableIdentifiers.getOperators().keySet() ) {
                    parserContext1.addInput( opKey, availableIdentifiers.getOperators().get( opKey ).getClass() );
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
                copyErrorLocation(e, context.getParentDescr());
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
                for ( Iterator<String> it = requiredInputs.iterator(); it.hasNext(); ) {
                    if ( PropertyTools.getFieldOrAccessor( availableIdentifiers.getThisClass(),
                                                           it.next() ) != null ) {
                        it.remove();
                    }
                }
            }

            // now, set the required input types and compile again
            final ParserContext parserContext2 = new ParserContext( conf );
            parserContext2.setStrictTypeEnforcement( true );
            parserContext2.setStrongTyping( true );
            parserContext2.setInterceptors( dialect.getInterceptors() );

            for ( String str : requiredInputs ) {
                Class< ? > cls;
                cls = availableIdentifiers.getDeclrClasses().get( str );
                if ( cls != null ) {
                    parserContext2.addInput( str,
                                             cls );
                    continue;
                }

                cls = availableIdentifiers.getGlobals().get( str );
                if ( cls != null ) {
                    parserContext2.addInput( str,
                                             cls );
                    continue;
                }

                cls = availableIdentifiers.getOperators().keySet().contains( str ) ? context.getConfiguration().getComponentFactory().getExpressionProcessor().getEvaluatorWrapperClass() : null;
                if ( cls != null ) {
                    parserContext2.addInput( str,
                                             cls );
                    continue;
                }

                if ( str.equals( contextIndeifier ) ) {
                    parserContext2.addInput( contextIndeifier,
                                             kcontextClass );
                } else if ( str.equals( "kcontext" ) ) {
                    parserContext2.addInput( "kcontext",
                                             kcontextClass );
                }
                if ( str.equals( "rule" ) ) {
                    parserContext2.addInput( "rule",
                                             Rule.class );
                }

                if ( localTypes != null ) {
                    cls = localTypes.get( str );
                    if ( cls != null ) {  
                        parserContext2.addInput( str,
                                                 cls );
                    }
                }
            }   
            
            if ( availableIdentifiers.getThisClass() != null ) {
                parserContext2.addInput( "this",
                                         availableIdentifiers.getThisClass() );
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
                    copyErrorLocation(e, context.getParentDescr());
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

            result = analyze( requiredInputs,
                              availableIdentifiers );

            result.setReturnType( returnType );

            result.setMvelVariables( variables );
            result.setTypesafe( typesafe );
        } else {
            result = analyze( (Set<String>) Collections.EMPTY_SET,
                              availableIdentifiers );
            result.setMvelVariables( new HashMap<String, Class< ? >>() );
            result.setTypesafe( true );

        }
        return result;
    }

    /**
     * Analyse an expression.
     *
     * @throws RecognitionException
     *             If an error occurs in the parser.
     */
    private MVELAnalysisResult analyze(final Set<String> identifiers,
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

        for ( Entry<String, Class< ? >> entry : availableIdentifiers.getGlobals().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedGlobals.put( entry.getKey(),
                                 entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        for ( Map.Entry<String, EvaluatorWrapper> op : availableIdentifiers.getOperators().entrySet() ) {
            if ( identifiers.contains( op.getKey() ) ) {
                usedOperators.put( op.getKey(),
                                   op.getValue() );
                notBound.remove( op.getKey() );
            }
        }

        result.setBoundIdentifiers( new BoundIdentifiers( usedDecls,
                                                          usedGlobals,
                                                          usedOperators,
                                                          availableIdentifiers.getThisClass() ) );
        result.setNotBoundedIdentifiers( notBound );

        return result;
    }
}
