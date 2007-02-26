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

package org.drools.semantics.java.builder;

import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;

/**
 * @author etirelli
 *
 */
public class EvalBuilder
    implements
    ConditionalElementBuilder {

    /**
     * Builds and returns an Eval Conditional Element
     * 
     * @param context The current build context
     * @param utils The current build utils instance
     * @param columnBuilder not used by EvalBuilder
     * @param descr The Eval Descriptor to build the eval conditional element from
     * 
     * @return the Eval Conditional Element
     */
    public ConditionalElement build(final BuildContext context,
                                    final BuildUtils utils,
                                    final ColumnBuilder columnBuilder,
                                    final BaseDescr descr) {
        // it must be an EvalDescr
        EvalDescr evalDescr = (EvalDescr) descr;

        final String className = "eval" + context.getNextId();
        
        evalDescr.setClassMethodName( className );

        final List[] usedIdentifiers = utils.getUsedIdentifiers( context,
                                                                 evalDescr,
                                                                 evalDescr.getText() );

        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
            declarations[i] = (Declaration) context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
        }

        final EvalCondition eval = new EvalCondition( declarations );

        StringTemplate st = utils.getRuleGroup().getInstanceOf( "evalMethod" );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           declarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        st.setAttribute( "methodName",
                         className );

        final String evalText = evalDescr.getText();
        st.setAttribute( "text",
                         evalText );

        context.getMethods().add( st.toString() );

        st = utils.getInvokerGroup().getInstanceOf( "evalInvoker" );

        st.setAttribute( "package",
                         context.getPkg().getName() );
        st.setAttribute( "ruleClassName",
                         utils.ucFirst( context.getRuleDescr().getClassName() ) );
        st.setAttribute( "invokerClassName",
                         context.getRuleDescr().getClassName() + utils.ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           declarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        st.setAttribute( "hashCode",
                         evalText.hashCode() );

        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + utils.ucFirst( className ) + "Invoker";
        context.getInvokers().put( invokerClassName,
                                   st.toString() );
        context.getInvokerLookups().put( invokerClassName,
                                         eval );
        context.getDescrLookups().put( invokerClassName,
                                       evalDescr );
        return eval;
    }

}
