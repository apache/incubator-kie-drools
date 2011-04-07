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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.definition.rule.Rule;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.runtime.rule.RuleContext;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.util.ParseTools;
import org.mvel2.util.PropertyTools;

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
     * @param availDecls
     *            Total set of declarations available.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     * @throws RecognitionException 
     *             If an error occurs in the parser.
     */
    public MVELAnalysisResult analyzeExpression(final PackageBuildContext context,
                                                final String expr,
                                                final BoundIdentifiers availableIdentifiers,
                                                final Map<String, Class<?>> localTypes,
                                                String contextIndeifier,
                                                Class kcontextClass) {
        MVELAnalysisResult result = null;
        if ( expr.trim().length() > 0 ) {
            MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            // creating a reusable parser configuration
            ParserConfiguration conf = new ParserConfiguration();
            conf.setImports( dialect.getImports() );
            conf.setPackageImports( (HashSet) dialect.getPackgeImports() );

            conf.setClassLoader( context.getPackageBuilder().getRootClassLoader() );          
            
            // first compilation is for verification only
            // @todo proper source file name
            final ParserContext parserContext1 = new ParserContext( conf );
            if ( localTypes != null ) {
                for ( Entry entry : localTypes.entrySet() ) {
                    parserContext1.addInput( (String) entry.getKey(), (Class) entry.getValue() );
                }
            }
            if ( availableIdentifiers.getThisClass() != null  ) {
                parserContext1.addInput( "this",
                                         availableIdentifiers.getThisClass());
            }            
            
            parserContext1.setStrictTypeEnforcement( false );
            parserContext1.setStrongTyping( false );
            parserContext1.setInterceptors( dialect.getInterceptors() );
            Class returnType  = null;
            
            try {
                returnType =  MVEL.analyze( expr,
                                            parserContext1 );
            } catch ( Exception e) {
                context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                              null,
                                                              null,
                                                              "Unable to Analyse Expression " + expr + ":\n" + e.getMessage() ) );
                return null;
            }            

            Set<String> requiredInputs = new HashSet(); 
            requiredInputs.addAll( parserContext1.getInputs().keySet() );
            HashMap<String, Class<?>> variables = (HashMap<String, Class<?>>) ((Map)parserContext1.getVariables());

            // MVEL includes direct fields of context object in non-strict mode. so we need to strip those
            if ( availableIdentifiers.getThisClass() != null ) {
                for ( Iterator<String> it = requiredInputs.iterator(); it.hasNext();) {
                    if (  PropertyTools.getFieldOrAccessor(  availableIdentifiers.getThisClass(), it.next()  ) != null ) {
                        it.remove();
                    }
                }
            }            

            // now, set the required input types and compile again
            final ParserContext parserContext2 = new ParserContext( conf );
            parserContext2.setStrictTypeEnforcement( true );
            parserContext2.setStrongTyping( true );
            parserContext2.setInterceptors( dialect.getInterceptors() );
            
            if ( context.isTypesafe() ) {            
                if ( localTypes != null ) {
                    for ( Entry<String, Class<?>> entry : localTypes.entrySet() ) {
                        parserContext2.addInput( entry.getKey(),
                                                 entry.getValue() );
                    }
                }
                
                for ( String str : requiredInputs ) {                    
                    Class cls = availableIdentifiers.getDeclarations().get( str );
                    if ( cls != null ) {
                        parserContext2.addInput( str,
                                                 cls );
                        continue;
                    }
                    
                    if ( cls == null ) {
                        cls = availableIdentifiers.getGlobals().get( str );
                        if ( cls != null ) {
                            parserContext2.addInput( str,
                                                     cls );
                            continue;
                        }
                    }
                    
                    if ( cls == null ) {
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
                    }
                }
                
                if ( availableIdentifiers.getThisClass() != null ) {
                    parserContext2.addInput( "this", availableIdentifiers.getThisClass() );
                }
    
                try {
                    returnType =  MVEL.analyze( expr,
                                                parserContext2 );
                } catch ( Exception e) {
                    context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                                  null,
                                                                  null,
                                                                  "Unable to Analyse Expression " + expr + ":\n" + e.getMessage() ) );
                    return null;
            }   
                
                requiredInputs = new HashSet();
                requiredInputs.addAll( parserContext2.getInputs().keySet() );
                requiredInputs.addAll(  variables.keySet() );
                variables = (HashMap<String, Class<?>>) ((Map)parserContext2.getVariables());
            }

            result = analyze( requiredInputs,
                              availableIdentifiers );
            
            result.setReturnType( returnType );
            
            result.setMvelVariables( variables );
        } else {
            result = analyze( (Set<String>) Collections.EMPTY_SET,
                              availableIdentifiers );
            result.setMvelVariables( new HashMap<String, Class<?>>() );

        }
        return result;
    }

    /**
     * Analyse an expression.
     * 
     * @param availDecls
     *            Total set of declarations available.
     * @param ast
     *            The AST for the expression.
     * 
     * @return The <code>Set</code> of declarations used by the expression.
     * 
     * @throws RecognitionException
     *             If an error occurs in the parser.
     */
    @SuppressWarnings("unchecked")
    private MVELAnalysisResult analyze(final Set<String> identifiers,
                                       final BoundIdentifiers availableIdentifiers) {

        MVELAnalysisResult result = new MVELAnalysisResult();
        result.setIdentifiers( identifiers );

        final Set<String> notBound = new HashSet<String>( identifiers );
        notBound.remove( "this" );
        Map<String, Class<?>> usedDecls = new HashMap<String, Class<?>>();
        Map<String, Class<?>> usedGlobals = new HashMap<String, Class<?>>();

        for ( Entry<String, Class<?>> entry : availableIdentifiers.getDeclarations().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedDecls.put( entry.getKey(),
                               entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        for ( Entry<String, Class<?>> entry : availableIdentifiers.getGlobals().entrySet() ) {
            if ( identifiers.contains( entry.getKey() ) ) {
                usedGlobals.put( entry.getKey(),
                               entry.getValue() );
                notBound.remove( entry.getKey() );
            }
        }

        result.setBoundIdentifiers( new BoundIdentifiers( usedDecls,
                                                          usedGlobals,
                                                          availableIdentifiers.getThisClass() ) );
        result.setNotBoundedIdentifiers( notBound );

        return result;
    }
}
