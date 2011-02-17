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

import java.util.List;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELPredicateExpression;
import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.RuleBuildContext;

/**
 * @author etirelli
 *
 */
public class MVELPredicateBuilder
    implements
    PredicateBuilder {

    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicate,
                      final PredicateDescr predicateDescr,
                      final AnalysisResult analysis) {
        MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );

        try {            
            Map< String , Class > declIds = context.getDeclarationResolver().getDeclarationClasses(context.getRule());
            
            Pattern p = ( Pattern ) context.getBuildStack().peek();            
            if (p.getObjectType() instanceof ClassObjectType ) {
                declIds.put( "this", ((ClassObjectType)p.getObjectType()).getClassType() );
            }    
            
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit((String) predicateDescr.getContent(), analysis,  previousDeclarations, localDeclarations, null, context);

            MVELPredicateExpression expr = new MVELPredicateExpression( unit,
                                                                        context.getDialect().getId());            
            predicate.setPredicateExpression( expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( context.getDialect().getId() );            
            data.addCompileable( predicate,
                                  expr );              

            expr.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                    predicateDescr,
                                                    e,
                                                    "Unable to build expression for 'inline-eval' : " + e.getMessage() + "'" + predicateDescr.getContent() + "'\n" + e.getMessage() ) );
        }
    }

}
