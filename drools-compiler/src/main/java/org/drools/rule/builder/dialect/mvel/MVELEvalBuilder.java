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
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELEvalExpression;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.ColumnBuilder;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.dialect.java.BuildUtils;
import org.mvel.MVEL;

/**
 * @author etirelli
 *
 */
public class MVELEvalBuilder
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
        final EvalDescr evalDescr = (EvalDescr) descr;

        final Declaration[] declarations = new Declaration[0];
        //        final List[] usedIdentifiers = utils.getUsedIdentifiers( context,
        //                                                                 evalDescr,
        //                                                                 evalDescr.getText() );
        //
        //        final Declaration[] declarations = new Declaration[usedIdentifiers[0].size()];
        //        for ( int i = 0, size = usedIdentifiers[0].size(); i < size; i++ ) {
        //            declarations[i] = (Declaration) context.getDeclarationResolver().getDeclaration( (String) usedIdentifiers[0].get( i ) );
        //        }

        final DroolsMVELFactory factory = new DroolsMVELFactory();
        factory.setPreviousDeclarationMap( context.getDeclarationResolver().getDeclarations() );
        factory.setGlobalsMap( context.getPkg().getGlobals() );

        final Serializable expr = MVEL.compileExpression( (String) evalDescr.getContent() );
        final EvalCondition eval = new EvalCondition( declarations );
        eval.setEvalExpression( new MVELEvalExpression( expr,
                                                        factory ) );

        return eval;
    }

}
