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
import java.util.Arrays;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.drools.RuntimeDroolsException;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.spi.PatternExtractor;
import org.drools.util.StringUtils;

/**
 * @author etirelli
 *
 */
public class JavaConsequenceBuilder
    implements
    ConsequenceBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConsequenceBuilder#buildConsequence(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public void build(final RuleBuildContext context,
                      final RuleDescr ruleDescr) {

        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        // generate 
        // generate Invoker
        final String className = "consequence";

        JavaDialect dialect = (JavaDialect) context.getDialect();

        StringTemplate st = dialect.getRuleGroup().getInstanceOf( "consequenceMethod" );

        st.setAttribute( "methodName",
                         className );

        final List[] usedIdentifiers = context.getDialect().getBlockIdentifiers( context,
                                                                                 ruleDescr,
                                                                                 (String) ruleDescr.getConsequence() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];

        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
        }

        dialect.setStringTemplateAttributes( context,
                                             st,
                                             declarations,
                                             (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );
        st.setAttribute( "text",
                         ((JavaDialect) context.getDialect()).getKnowledgeHelperFixer().fix( (String) ruleDescr.getConsequence() ) );

        context.getMethods().add( st.toString() );

        st = dialect.getInvokerGroup().getInstanceOf( "consequenceInvoker" );

        st.setAttribute( "package",
                         context.getPkg().getName() );
        st.setAttribute( "ruleClassName",
                         StringUtils.ucFirst( context.getRuleDescr().getClassName() ) );
        st.setAttribute( "invokerClassName",
                         ruleDescr.getClassName() + StringUtils.ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        dialect.setStringTemplateAttributes( context,
                                             st,
                                             declarations,
                                             (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        // Must use the rule declarations, so we use the same order as used in the generated invoker
        final List list = Arrays.asList( context.getRule().getDeclarations() );

        //final int[] indexes = new int[declarations.length];
        final List indexes = new ArrayList( declarations.length );

        // have to user a String[] as boolean[] is broken in stringtemplate
        final String[] notPatterns = new String[declarations.length];
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            indexes.add( i,
                         new Integer( list.indexOf( declarations[i] ) ) );
            notPatterns[i] = (declarations[i].getExtractor() instanceof PatternExtractor) ? null : "true";
            if ( ((Integer) indexes.get( i )).intValue() == -1 ) {
                context.getErrors().add( new RuleError(context.getRule(), ruleDescr, null, "Internal Error : Unable to find declaration in list while generating the consequence invoker" ) );                
            }
        }

        st.setAttribute( "indexes",
                         indexes );

        st.setAttribute( "notPatterns",
                         notPatterns );

        st.setAttribute( "text",
                         ruleDescr.getConsequence() );

        final String invokerClassName = context.getPkg().getName() + "." + ruleDescr.getClassName() + StringUtils.ucFirst( className ) + "Invoker";

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
