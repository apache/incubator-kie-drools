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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.drools.base.ClassObjectType;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Accumulate;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.Dialect;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;

/**
 * A builder for the java dialect accumulate version
 * 
 * @author etirelli
 */
public class JavaAccumulateBuilder extends AbstractJavaBuilder
    implements
    ConditionalElementBuilder,
    AccumulateBuilder {

    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final Pattern prefixPattern) {

        final AccumulateDescr accumDescr = (AccumulateDescr) descr;

        final PatternBuilder patternBuilder = (PatternBuilder) context.getDialect().getBuilder( PatternDescr.class );

        final Pattern sourcePattern = patternBuilder.build( context,
                                                            accumDescr.getSourcePattern() );

        if ( sourcePattern == null ) {
            return null;
        }

        final Pattern resultPattern = patternBuilder.build( context,
                                                            accumDescr.getResultPattern() );

        final String className = "Accumulate" + context.getNextId();
        accumDescr.setClassName( className );

        final JavaAnalysisResult analysis1 = (JavaAnalysisResult) context.getDialect().analyzeBlock( context,
                                                                                                     accumDescr,
                                                                                                     accumDescr.getInitCode() );
        final Dialect.AnalysisResult analysis2 = context.getDialect().analyzeBlock( context,
                                                                                    accumDescr,
                                                                                    accumDescr.getActionCode() );
        final Dialect.AnalysisResult analysis3 = context.getDialect().analyzeExpression( context,
                                                                                         accumDescr,
                                                                                         accumDescr.getResultCode() );

        final List requiredDeclarations = new ArrayList( analysis1.getBoundIdentifiers()[0] );
        requiredDeclarations.addAll( analysis2.getBoundIdentifiers()[0] );
        requiredDeclarations.addAll( analysis3.getBoundIdentifiers()[0] );

        final List requiredGlobals = new ArrayList( analysis1.getBoundIdentifiers()[1] );
        requiredGlobals.addAll( analysis2.getBoundIdentifiers()[1] );
        requiredGlobals.addAll( analysis3.getBoundIdentifiers()[1] );

        final Declaration[] declarations = new Declaration[requiredDeclarations.size()];
        for ( int i = 0, size = requiredDeclarations.size(); i < size; i++ ) {
            declarations[i] = context.getDeclarationResolver().getDeclaration( (String) requiredDeclarations.get( i ) );
        }
        final Declaration[] sourceDeclArr = (Declaration[]) sourcePattern.getOuterDeclarations().values().toArray( new Declaration[0] );

        final String[] globals = (String[]) requiredGlobals.toArray( new String[requiredGlobals.size()] );

        final Map map = createVariableContext( className,
                                               null,
                                               context,
                                               declarations,
                                               null,
                                               globals );

        map.put( "className",
                 accumDescr.getClassName() );
        map.put( "innerDeclarations",
                 sourceDeclArr );

        final String initCode = this.fixInitCode( analysis1,
                                                  accumDescr.getInitCode() );
        final String actionCode = accumDescr.getActionCode();
        final String resultCode = accumDescr.getResultCode();

        String[] attributesTypes = new String[analysis1.getLocalVariablesMap().size()];
        String[] attributes = new String[analysis1.getLocalVariablesMap().size()];
        int index = 0;
        for ( Iterator it = analysis1.getLocalVariablesMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            attributes[index] = (String) entry.getKey();
            attributesTypes[index] = ((JavaLocalDeclarationDescr) entry.getValue()).getType();
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

        String resultType = null;
        // TODO: Need to change this... 
        if ( resultPattern.getObjectType() instanceof ClassObjectType ) {
            resultType = ((ClassObjectType) resultPattern.getObjectType()).getClassType().getName();
        } else {
            resultType = resultPattern.getObjectType().getValueType().getClassType().getName();
        }

        map.put( "resultType",
                 resultType );

        map.put( "hashCode",
                 new Integer( actionCode.hashCode() ) );

        final Accumulate accumulate = new Accumulate( sourcePattern,
                                                      resultPattern,
                                                      declarations,
                                                      sourceDeclArr );

        generatTemplates( "accumulateInnerClass",
                          "accumulateInvoker",
                          context,
                          className,
                          map,
                          accumulate,
                          accumDescr );

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

        StringBuffer initCode = new StringBuffer();
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

        // TODO Auto-generated method stub
        return initCode.toString();
    }
}
