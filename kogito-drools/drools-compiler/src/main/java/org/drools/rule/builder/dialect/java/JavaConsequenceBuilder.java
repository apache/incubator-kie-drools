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

import java.util.Arrays;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.drools.RuntimeDroolsException;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.BuildUtils;
import org.drools.rule.builder.ConsequenceBuilder;

/**
 * @author etirelli
 *
 */
public class JavaConsequenceBuilder implements ConsequenceBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void buildConsequence(final BuildContext context,
                                 final BuildUtils utils,
                                 final RuleDescr ruleDescr) {
        
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );
        
        // generate 
        // generate Invoker
        final String className = "consequence";

        StringTemplate st = utils.getRuleGroup().getInstanceOf( "consequenceMethod" );

        st.setAttribute( "methodName",
                         className );

        final List[] usedIdentifiers = utils.getUsedCIdentifiers( context,
                                                                  ruleDescr,
                                                                  ruleDescr.getConsequence() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
        }

        utils.setStringTemplateAttributes( context,
                                           st,
                                           declarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );
        st.setAttribute( "text",
                         utils.getKnowledgeHelperFixer().fix( ruleDescr.getConsequence() ) );

        context.getMethods().add( st.toString() );

        st = utils.getInvokerGroup().getInstanceOf( "consequenceInvoker" );

        st.setAttribute( "package",
                         context.getPkg().getName() );
        st.setAttribute( "ruleClassName",
                         utils.ucFirst( context.getRuleDescr().getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + utils.ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           declarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        // Must use the rule declarations, so we use the same order as used in the generated invoker
        final List list = Arrays.asList( context.getRule().getDeclarations() );

        final int[] indexes = new int[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes[i] = list.indexOf( declarations[i] );
            if ( indexes[i] == -1 ) {
                // some defensive code, this should never happen
                throw new RuntimeDroolsException( "Unable to find declaration in list while generating the consequence invoker" );
            }
        }

        st.setAttribute( "indexes",
                         indexes );

        st.setAttribute( "text",
                         ruleDescr.getConsequence() );

        final String invokerClassName = context.getPkg().getName() + "." + ruleDescr.getClassName() + utils.ucFirst( className ) + "Invoker";
        context.getInvokers().put( invokerClassName,
                                   st.toString() );
        context.getInvokerLookups().put( invokerClassName,
                                         context.getRule() );
        context.getDescrLookups().put( invokerClassName,
                                       ruleDescr );
        
        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }

}
