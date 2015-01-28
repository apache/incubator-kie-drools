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
import org.drools.compiler.lang.descr.ReturnValueRestrictionDescr;
import org.drools.compiler.rule.builder.ReturnValueBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELReturnValueExpression;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.ReturnValueRestriction;
import org.drools.core.spi.KnowledgeHelper;

import java.util.Map;

public class MVELReturnValueBuilder
    implements
        ReturnValueBuilder {

    public void build(final RuleBuildContext context,
                      final BoundIdentifiers usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr,
                      final AnalysisResult analysis) {
        boolean typesafe = context.isTypesafe();
        try {
            MVELDialect dialect = (MVELDialect) context.getDialect( context.getDialect().getId() );
            
            Map< String , Class<?> > declIds = context.getDeclarationResolver().getDeclarationClasses(context.getRule());
            
            Pattern p = ( Pattern ) context.getBuildStack().peek();
            
            context.setTypesafe( ((MVELAnalysisResult)analysis).isTypesafe() );
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit((String) returnValueRestrictionDescr.getContent(), 
                                                                      analysis,  
                                                                      previousDeclarations, 
                                                                      localDeclarations, 
                                                                      null, 
                                                                      context,
                                                                      "drools",
                                                                      KnowledgeHelper.class,
                                                                      false );
    
            MVELReturnValueExpression expr = new MVELReturnValueExpression( unit,
                                                                            context.getDialect().getId() );
            returnValueRestriction.setReturnValueExpression( expr );
            
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( returnValueRestriction,
                                  expr );
            
            expr.compile( data, context.getRule() );
        } catch ( final Exception e ) {
            DialectUtil.copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'returnValue' : " + e.getMessage() + "'" + context.getRuleDescr().getSalience() + "'" ) );
        } finally {
            context.setTypesafe( typesafe );
        }

    }

}
