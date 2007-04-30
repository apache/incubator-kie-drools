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
import java.util.List;
import java.util.Map;

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
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.RuleBuildContext;

/**
 * @author etirelli
 *
 */
public class JavaAccumulateBuilder extends AbstractJavaBuilder
    implements
    ConditionalElementBuilder,
    AccumulateBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.PatternBuilder, org.drools.lang.descr.BaseDescr)
     */
    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.AccumulateBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.PatternBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(final RuleBuildContext context,
                                    final BaseDescr descr) {

        final AccumulateDescr accumDescr = (AccumulateDescr) descr;

        final PatternBuilder patternBuilder = (PatternBuilder) context.getDialect().getBuilder( PatternDescr.class );

        final Pattern sourcePattern = patternBuilder.build( context,
                                                            accumDescr.getSourcePattern() );

        if ( sourcePattern == null ) {
            return null;
        }

        final Pattern resultPattern = patternBuilder.build( context,
                                                            accumDescr.getResultPattern() );

        final String className = "accumulate" + context.getNextId();
        accumDescr.setClassMethodName( className );

        final List[] usedIdentifiers1 = context.getDialect().getBlockIdentifiers( context,
                                                                                  accumDescr,
                                                                                  accumDescr.getInitCode() );
        final List[] usedIdentifiers2 = context.getDialect().getBlockIdentifiers( context,
                                                                                  accumDescr,
                                                                                  accumDescr.getActionCode() );
        final List[] usedIdentifiers3 = context.getDialect().getExpressionIdentifiers( context,
                                                                                       accumDescr,
                                                                                       accumDescr.getResultCode() );

        final List requiredDeclarations = new ArrayList( usedIdentifiers1[0] );
        requiredDeclarations.addAll( usedIdentifiers2[0] );
        requiredDeclarations.addAll( usedIdentifiers3[0] );

        final List requiredGlobals = new ArrayList( usedIdentifiers1[1] );
        requiredGlobals.addAll( usedIdentifiers2[1] );
        requiredGlobals.addAll( usedIdentifiers3[1] );

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

        map.put( "innerDeclarations",
                 sourceDeclArr );

        final String initCode = accumDescr.getInitCode();
        final String actionCode = accumDescr.getActionCode();
        final String resultCode = accumDescr.getResultCode();
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

        generatTemplates( "accumulateMethod",
                          "accumulateInvoker",
                          context,
                          className,
                          map,
                          accumulate,
                          accumDescr );

        return accumulate;
    }

}
