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

package org.drools.rule.builder.dialect.java;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.base.accumulators.JavaAccumulatorFunctionExecutor;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AccumulateDescr.AccumulateFunctionCallDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.runtime.rule.AccumulateFunction;
import org.drools.spi.Accumulator;

/**
 * A builder for the java dialect accumulate version
 */
public class JavaAccumulateBuilder extends AbstractJavaRuleBuilder
    implements
    AccumulateBuilder {

    public RuleConditionElement build( final RuleBuildContext context,
                                       final BaseDescr descr ) {
        return build( context,
                      descr,
                      null );
    }

    @SuppressWarnings("unchecked")
    public RuleConditionElement build( final RuleBuildContext context,
                                       final BaseDescr descr,
                                       final Pattern prefixPattern ) {

        final AccumulateDescr accumDescr = (AccumulateDescr) descr;

        if ( !accumDescr.hasValidInput() ) {
            return null;
        }

        final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( accumDescr.getInput().getClass() );

        final RuleConditionElement source = builder.build( context,
                                                           accumDescr.getInput() );
        
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
        Map<String, Class<?>> declCls = context.getDeclarationResolver().getDeclarationClasses( decls );

        if ( source == null ) {
            return null;
        }

        Accumulate accumulate = null;

        if ( accumDescr.isExternalFunction() ) {
            // if it is an external function, build a method for it
            // TODO: add the support to multiple functions
            AccumulateFunctionCallDescr func = accumDescr.getFunctions().get( 0 );
            AccumulateFunction function = context.getConfiguration().getAccumulateFunction( func.getFunction() );

            if ( function == null ) {
                context.getErrors().add( new DescrBuildError( accumDescr,
                                                              context.getRuleDescr(),
                                                              null,
                                                              "Unknown accumulate function: '" + func.getFunction() + "' on rule '" + context.getRuleDescr().getName() + "'. All accumulate functions must be registered before building a resource." ) );
                return null;
            }

            final JavaAnalysisResult analysis = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                                                        accumDescr,
                                                                                                        func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                                                                                                        new BoundIdentifiers( declCls, context.getPackageBuilder().getGlobals() ) );

            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

            final Declaration[] previousDeclarations = usedIdentifiers.getDeclrClasses().values().toArray(new Declaration[usedIdentifiers.getDeclrClasses().size()]);
            final Declaration[] sourceDeclArr = (Declaration[]) source.getOuterDeclarations().values().toArray( new Declaration[0] );

            final String className = "accumulateExpression" + context.getNextId();

            final Map<String, Object> map = createVariableContext( className,
                                                                   func.getParams().length > 0 ? func.getParams()[0] : "\"\"",
                                                                   context,
                                                                   previousDeclarations,
                                                                   sourceDeclArr,
                                                                   usedIdentifiers.getGlobals(),
                                                                   null );
            map.put( "readLocalsFromTuple",
                     accumDescr.isMultiPattern() ? Boolean.TRUE : Boolean.FALSE );

            JavaAccumulatorFunctionExecutor accumulator = new JavaAccumulatorFunctionExecutor( function );

            accumulate = new Accumulate( source,
                                         previousDeclarations,
                                         sourceDeclArr,
                                         new Accumulator[]{accumulator} );

            generatTemplates( "returnValueMethod",
                              "returnValueInvoker",
                              context,
                              className,
                              map,
                              accumulator,
                              accumDescr );
        } else {
            // ELSE, if it is not an external function, build it using the regular java builder
            final String className = "Accumulate" + context.getNextId();
            accumDescr.setClassName( className );

            BoundIdentifiers available = new BoundIdentifiers( declCls, context.getPackageBuilder().getGlobals() );

            final JavaAnalysisResult initCodeAnalysis = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                                                                accumDescr,
                                                                                                                accumDescr.getInitCode(),
                                                                                                                available );
            final AnalysisResult actionCodeAnalysis = context.getDialect().analyzeBlock( context,
                                                                                         accumDescr,
                                                                                         accumDescr.getActionCode(),
                                                                                         available );
            
            final AnalysisResult resultCodeAnalysis = context.getDialect().analyzeExpression( context,
                                                                                                      accumDescr,
                                                                                                      accumDescr.getResultCode(),
                                                                                                      available );

            final Set<String> requiredDeclarations = new HashSet<String>( initCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
            requiredDeclarations.addAll( actionCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
            requiredDeclarations.addAll( resultCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );

            final Map<String, Class<?>> requiredGlobals = new HashMap<String, Class<?>>( initCodeAnalysis.getBoundIdentifiers().getGlobals() );
            requiredGlobals.putAll( actionCodeAnalysis.getBoundIdentifiers().getGlobals() );
            requiredGlobals.putAll( resultCodeAnalysis.getBoundIdentifiers().getGlobals() );

            if ( accumDescr.getReverseCode() != null ) {
                final AnalysisResult reverseCodeAnalysis = context.getDialect().analyzeBlock( context,
                                                                                                      accumDescr,
                                                                                                      accumDescr.getActionCode(),
                                                                                                      available );
                requiredDeclarations.addAll( reverseCodeAnalysis.getBoundIdentifiers().getDeclrClasses().keySet() );
                requiredGlobals.putAll( reverseCodeAnalysis.getBoundIdentifiers().getGlobals() );
            }

            
            
            final Declaration[] declarations = new Declaration[requiredDeclarations.size()];
            int i = 0;
            for ( Iterator<String> it = requiredDeclarations.iterator(); it.hasNext(); i++ ) {
                declarations[i] = decls.get( it.next() );
            }
            final Declaration[] sourceDeclArr = (Declaration[]) source.getOuterDeclarations().values().toArray( new Declaration[0] );

            final Map<String, Object> map = createVariableContext( className,
                                                                   null,
                                                                   context,
                                                                   declarations,
                                                                   null,
                                                                   requiredGlobals,
                                                                   null );

            map.put( "className",
                     accumDescr.getClassName() );
            map.put( "innerDeclarations",
                     sourceDeclArr );
            map.put( "isMultiPattern",
                     accumDescr.isMultiPattern() ? Boolean.TRUE : Boolean.FALSE );

            final String initCode = this.fixInitCode( initCodeAnalysis,
                                                      accumDescr.getInitCode() );
            final String actionCode = accumDescr.getActionCode();
            final String resultCode = accumDescr.getResultCode();

            String[] attributesTypes = new String[initCodeAnalysis.getLocalVariablesMap().size()];
            String[] attributes = new String[initCodeAnalysis.getLocalVariablesMap().size()];
            int index = 0;
            for ( Map.Entry<String, JavaLocalDeclarationDescr> entry : initCodeAnalysis.getLocalVariablesMap().entrySet() ) {
                attributes[index] = entry.getKey();
                attributesTypes[index] = entry.getValue().getType();
                index++;
            }

            map.put( "attributes",
                     attributes );
            map.put( "attributesTypes",
                     attributesTypes );

            map.put( "initCode",
                     initCode );
            map.put( "actionCode",
                     actionCode );
            map.put( "resultCode",
                     resultCode );
            if ( accumDescr.getReverseCode() == null ) {
                map.put( "reverseCode",
                         "" );
                map.put( "supportsReverse",
                         "false" );
            } else {
                map.put( "reverseCode",
                         accumDescr.getReverseCode() );
                map.put( "supportsReverse",
                         "true" );
            }

            map.put( "hashCode",
                     new Integer( actionCode.hashCode() ) );

            accumulate = new Accumulate( source,
                                         declarations,
                                         sourceDeclArr );

            generatTemplates( "accumulateInnerClass",
                              "accumulateInvoker",
                              context,
                              className,
                              map,
                              accumulate,
                              accumDescr );
        }

        return accumulate;
    }

    @SuppressWarnings("unchecked")
    protected String fixInitCode( JavaAnalysisResult analysis,
                                  final String originalCode ) {
        TreeSet<JavaLocalDeclarationDescr> locals = new TreeSet<JavaLocalDeclarationDescr>( new Comparator<JavaLocalDeclarationDescr>() {
            public int compare( JavaLocalDeclarationDescr o1,
                                JavaLocalDeclarationDescr o2 ) {
                return o1.getStart() - o2.getStart();
            }
        } );

        for ( Iterator it = analysis.getLocalVariablesMap().values().iterator(); it.hasNext(); ) {
            locals.add( (JavaLocalDeclarationDescr) it.next() );
        }

        StringBuilder initCode = new StringBuilder();
        int lastAdded = 0;
        for ( Iterator it = locals.iterator(); it.hasNext(); ) {
            JavaLocalDeclarationDescr d = (JavaLocalDeclarationDescr) it.next();
            // adding chunk
            initCode.append( originalCode.substring( lastAdded,
                                                     d.getStart() ) );
            lastAdded = d.getEnd();
            // adding variable initializations
            for ( Iterator vars = d.getIdentifiers().iterator(); vars.hasNext(); ) {
                JavaLocalDeclarationDescr.IdentifierDescr id = (JavaLocalDeclarationDescr.IdentifierDescr) vars.next();
                initCode.append( originalCode.substring( id.getStart(),
                                                         id.getEnd() ) );
                initCode.append( ";" );
                lastAdded = id.getEnd();
                while ( lastAdded < originalCode.length() && (Character.isWhitespace( originalCode.charAt( lastAdded ) ) || originalCode.charAt( lastAdded ) == ';') ) {
                    lastAdded++;
                }
            }
        }
        initCode.append( originalCode.substring( lastAdded ) );

        return initCode.toString();
    }

}
