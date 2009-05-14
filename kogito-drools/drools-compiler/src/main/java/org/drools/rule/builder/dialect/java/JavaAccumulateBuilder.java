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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.drools.base.accumulators.JavaAccumulatorFunctionExecutor;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.AccumulateDescr;
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

/**
 * A builder for the java dialect accumulate version
 * 
 * @author etirelli
 */
public class JavaAccumulateBuilder extends AbstractJavaRuleBuilder
    implements
    AccumulateBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {

        final AccumulateDescr accumDescr = (AccumulateDescr) descr;

        if ( !accumDescr.hasValidInput() ) {
            return null;
        }

        final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( accumDescr.getInput().getClass() );

        final RuleConditionElement source = builder.build( context,
                                                           accumDescr.getInput() );

        if ( source == null ) {
            return null;
        }

        Accumulate accumulate = null;

        if ( accumDescr.isExternalFunction() ) {
            // if it is an external function, build a method for it

            final JavaAnalysisResult analysis = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                                                        accumDescr,
                                                                                                        accumDescr.getExpression(),
                                                                                                        new Map[]{context.getDeclarationResolver().getDeclarationClasses(context.getRule()), context.getPackageBuilder().getGlobals()} );

            final List[] usedIdentifiers = analysis.getBoundIdentifiers();

            final List<Declaration> tupleDeclarations = new ArrayList<Declaration>();
            for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
                tupleDeclarations.add( context.getDeclarationResolver().getDeclaration(context.getRule(), (String) usedIdentifiers[0].get( i ) ) );
            }

            final Declaration[] previousDeclarations = tupleDeclarations.toArray( new Declaration[tupleDeclarations.size()] );
            final String[] requiredGlobals = (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] );
            final Declaration[] sourceDeclArr = (Declaration[]) source.getOuterDeclarations().values().toArray( new Declaration[0] );

            final String className = "accumulateExpression" + context.getNextId();

            final Map<String, Object> map = createVariableContext( className,
                                                   (String) accumDescr.getExpression(),
                                                   context,
                                                   previousDeclarations,
                                                   sourceDeclArr,
                                                   requiredGlobals );
            map.put( "readLocalsFromTuple", accumDescr.isMultiPattern() ? Boolean.TRUE : Boolean.FALSE );

            AccumulateFunction function = context.getConfiguration().getAccumulateFunction( accumDescr.getFunctionIdentifier() );

            JavaAccumulatorFunctionExecutor accumulator = new JavaAccumulatorFunctionExecutor( function );

            accumulate = new Accumulate( source,
                                         previousDeclarations,
                                         sourceDeclArr,
                                         accumulator );

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

            Map<String,Class<?>>[] available = new Map[]{context.getDeclarationResolver().getDeclarationClasses(context.getRule()), context.getPackageBuilder().getGlobals()};
            final JavaAnalysisResult initCodeAnalysis = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                                                                accumDescr,
                                                                                                                accumDescr.getInitCode(),
                                                                                                                available );
            final Dialect.AnalysisResult actionCodeAnalysis = context.getDialect().analyzeBlock( context,
                                                                                                 accumDescr,
                                                                                                 accumDescr.getActionCode(),
                                                                                                 available );
            final Dialect.AnalysisResult resultCodeAnalysis = context.getDialect().analyzeExpression( context,
                                                                                                      accumDescr,
                                                                                                      accumDescr.getResultCode(),
                                                                                                      available );

            final Set<String> requiredDeclarations = new HashSet<String>( initCodeAnalysis.getBoundIdentifiers()[0] );
            requiredDeclarations.addAll( actionCodeAnalysis.getBoundIdentifiers()[0] );
            requiredDeclarations.addAll( resultCodeAnalysis.getBoundIdentifiers()[0] );

            final Set<String> requiredGlobals = new HashSet<String>( initCodeAnalysis.getBoundIdentifiers()[1] );
            requiredGlobals.addAll( actionCodeAnalysis.getBoundIdentifiers()[1] );
            requiredGlobals.addAll( resultCodeAnalysis.getBoundIdentifiers()[1] );

            if ( accumDescr.getReverseCode() != null ) {
                final Dialect.AnalysisResult reverseCodeAnalysis = context.getDialect().analyzeBlock( context,
                                                                                                      accumDescr,
                                                                                                      accumDescr.getActionCode(),
                                                                                                      available );
                requiredDeclarations.addAll( reverseCodeAnalysis.getBoundIdentifiers()[0] );
                requiredGlobals.addAll( reverseCodeAnalysis.getBoundIdentifiers()[1] );
            }

            final Declaration[] declarations = new Declaration[requiredDeclarations.size()];
            int i = 0;
            for( Iterator<String> it = requiredDeclarations.iterator(); it.hasNext(); i++ ) {
                declarations[i] = context.getDeclarationResolver().getDeclaration(context.getRule(), it.next() );
            }
            final Declaration[] sourceDeclArr = (Declaration[]) source.getOuterDeclarations().values().toArray( new Declaration[0] );

            final String[] globals = requiredGlobals.toArray( new String[requiredGlobals.size()] );

            final Map<String, Object> map = createVariableContext( className,
                                                   null,
                                                   context,
                                                   declarations,
                                                   null,
                                                   globals );

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

    protected String fixInitCode(JavaAnalysisResult analysis,
                                 final String originalCode) {
        TreeSet locals = new TreeSet( new Comparator() {
            public int compare(Object o1,
                               Object o2) {
                JavaLocalDeclarationDescr d1 = (JavaLocalDeclarationDescr) o1;
                JavaLocalDeclarationDescr d2 = (JavaLocalDeclarationDescr) o2;
                return d1.getStart() - d2.getStart();
            }
        } );

        for ( Iterator it = analysis.getLocalVariablesMap().values().iterator(); it.hasNext(); ) {
            locals.add( it.next() );
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
