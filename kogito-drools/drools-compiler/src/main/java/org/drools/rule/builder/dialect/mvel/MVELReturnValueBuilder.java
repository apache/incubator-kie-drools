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

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELReturnValueExpression;
import org.drools.compiler.Dialect;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel.ExpressionCompiler;
import org.mvel.MVEL;
import org.mvel.ParserContext;

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
        
        final DroolsMVELFactory factory = new DroolsMVELFactory(previousMap, localMap,  context.getPkg().getGlobals() );
        factory.setNextFactory( ((MVELDialect)context.getDialect()).getClassImportResolverFactory() );

        final ParserContext parserContext = new ParserContext(((MVELDialect) context.getDialect()).getClassImportResolverFactory().getImportedClasses(), null, null);
        parserContext.setStrictTypeEnforcement( true );
        
//        ExpressionCompiler compiler = new ExpressionCompiler( (String) returnValueRestrictionDescr.getContent() );
//        final Serializable expr = compiler.compile( parserContext );
        
        Dialect.AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                                  returnValueRestrictionDescr,
                                                                                  returnValueRestrictionDescr.getContent() );
        
        final Serializable expr = ((MVELDialect) context.getDialect()).compile( (String) returnValueRestrictionDescr.getContent(), analysis, null, null, context );        
        
        returnValueRestriction.setReturnValueExpression( new MVELReturnValueExpression( expr,
                                                                                        factory ) );
    }

}
