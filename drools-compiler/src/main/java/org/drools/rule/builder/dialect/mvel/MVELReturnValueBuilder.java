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

package org.drools.rule.builder.dialect.mvel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELReturnValueExpression;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;

/**
 * @author etirelli
 *
 */
public class MVELReturnValueBuilder
    implements
    ReturnValueBuilder {

    public void build(final RuleBuildContext context,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr) {

        Map previousMap = new HashMap();
        for ( int i = 0, length = previousDeclarations.length; i < length; i++ ) {
            previousMap.put( previousDeclarations[i].getIdentifier(),
                             previousDeclarations[i] );
        }

        Map localMap = new HashMap();
        for ( int i = 0, length = localDeclarations.length; i < length; i++ ) {
            localMap.put( localDeclarations[i].getIdentifier(),
                          localDeclarations[i] );
        }

        final DroolsMVELFactory factory = new DroolsMVELFactory( previousMap,
                                                                 localMap,
                                                                 context.getPkg().getGlobals() );

        MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
        factory.setNextFactory( data.getFunctionFactory() );

        Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                  returnValueRestrictionDescr,
                                                                                  returnValueRestrictionDescr.getContent(),
                                                                                  new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );

        final Serializable expr = ((MVELDialect) context.getDialect()).compile( (String) returnValueRestrictionDescr.getContent(),
                                                                                analysis,
                                                                                null,
                                                                                null,
                                                                                null,
                                                                                context );

        returnValueRestriction.setReturnValueExpression( new MVELReturnValueExpression( expr,
                                                                                        factory ) );
    }

}
