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

import org.antlr.stringtemplate.StringTemplate;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELEvalExpression;
import org.drools.base.mvel.MVELPredicateExpression;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.ColumnBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.dialect.java.BuildUtils;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.DeclarationScopeResolver;
import org.mvel.MVEL;

/**
 * @author etirelli
 *
 */
public class MVELPredicateBuilder
    implements
    PredicateBuilder {

    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final PredicateConstraint predicate,
                      final PredicateDescr predicateDescr) {

        //final Declaration[] declarations = new Declaration[0];
//        final List[] usedIdentifiers = utils.getUsedIdentifiers( context,
//                                                                 evalDescr,
//                                                                 evalDescr.getText() );
//
//        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
//        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
//            declarations[i] = (Declaration) context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
//        }
        
        DroolsMVELFactory factory = new DroolsMVELFactory( );
        
        Map map = new HashMap();
        for ( int i = 0, length = previousDeclarations.length; i < length; i++ ) {
            map.put( previousDeclarations[i].getIdentifier(), previousDeclarations[i] );
        }        
        factory.setPreviousDeclarationMap( map );
        
        map = new HashMap();
        for ( int i = 0, length = localDeclarations.length; i < length; i++ ) {
            map.put( localDeclarations[i].getIdentifier(), localDeclarations[i] );
        }                
        factory.setLocalDeclarationMap( map );
        
        factory.setGlobalsMap( context.getPkg().getGlobals() );        
        
        Serializable expr = MVEL.compileExpression( (String) predicateDescr.getContent() );
        predicate.setPredicateExpression( new MVELPredicateExpression(expr,factory) );        
    }
    
}
