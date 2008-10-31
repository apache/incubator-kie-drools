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
import java.util.Set;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELReturnValueExpression;
import org.drools.compiler.DescrBuildError;
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

        try {  
            MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );
    
            Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                      returnValueRestrictionDescr,
                                                                                      returnValueRestrictionDescr.getContent(),
                                                                                      new Set[]{context.getDeclarationResolver().getDeclarations(context.getRule()).keySet(), context.getPkg().getGlobals().keySet()} );
    
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit((String) returnValueRestrictionDescr.getContent(), analysis,  previousDeclarations, localDeclarations, null, context);
    
            MVELReturnValueExpression expr = new MVELReturnValueExpression( unit,
                                                                            context.getDialect().getId() );
            returnValueRestriction.setReturnValueExpression( expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( context.getDialect().getId() );
            data.addCompileable( returnValueRestriction,
                                  expr );
            
            expr.compile( context.getPackageBuilder().getRootClassLoader() );
        } catch ( final Exception e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'returnValue' : " + e.getMessage() + "'" + context.getRuleDescr().getSalience() + "'" ) );
        }            
    }

}
