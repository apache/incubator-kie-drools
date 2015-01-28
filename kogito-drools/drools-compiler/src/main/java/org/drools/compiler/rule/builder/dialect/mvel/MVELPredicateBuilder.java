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

package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.lang.descr.PredicateDescr;
import org.drools.compiler.rule.builder.PredicateBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELPredicateExpression;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.KnowledgeHelper;

import java.util.Map;

import static org.drools.compiler.rule.builder.dialect.DialectUtil.copyErrorLocation;

public class MVELPredicateBuilder
    implements
    PredicateBuilder {

    public void build( final RuleBuildContext context,
                       final BoundIdentifiers usedIdentifiers,
                       final Declaration[] previousDeclarations,
                       final Declaration[] localDeclarations,
                       final PredicateConstraint predicate,
                       final PredicateDescr predicateDescr,
                       final AnalysisResult analysis ) {
        boolean typesafe = context.isTypesafe();

        if (typesafe && analysis instanceof MVELAnalysisResult) {
            Class<?> returnClass = ((MVELAnalysisResult)analysis).getReturnType();
            if (returnClass != Boolean.class && returnClass != Boolean.TYPE) {
                context.addError( new DescrBuildError( context.getParentDescr(),
                                                              predicateDescr,
                                                              null,
                                                              "Predicate '" + predicateDescr.getContent() + "' must be a Boolean expression\n" + predicateDescr.positionAsString() ) );
            }
        }

        MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );

        try {
            Map<String, Class< ? >> declIds = context.getDeclarationResolver().getDeclarationClasses( context.getRule() );

            Pattern p = (Pattern) context.getBuildStack().peek();
            if ( p.getObjectType() instanceof ClassObjectType ) {
                declIds.put( "this",
                             ((ClassObjectType) p.getObjectType()).getClassType() );
            }
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit((String) predicateDescr.getContent(), 
                                                                      analysis,  
                                                                      previousDeclarations, 
                                                                      localDeclarations, 
                                                                      null, 
                                                                      context,
                                                                      "drools",
                                                                      KnowledgeHelper.class,
                                                                      false );

            MVELPredicateExpression expr = new MVELPredicateExpression( unit,
                                                                        context.getDialect().getId() );
            predicate.setPredicateExpression( expr );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( predicate,
                                  expr );

            expr.compile( data, context.getRule() );
        } catch ( final Exception e ) {
            copyErrorLocation(e, predicateDescr);
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          predicateDescr,
                                                          e,
                                                          "Unable to build expression for 'inline-eval' : " + e.getMessage() + "'" + predicateDescr.getContent() + "'\n" + e.getMessage() ) );
        } finally {
            context.setTypesafe( typesafe );
        }
    }

}
