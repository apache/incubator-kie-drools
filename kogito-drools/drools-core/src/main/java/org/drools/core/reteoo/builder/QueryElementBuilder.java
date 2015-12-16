/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.builder;

import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.RuleConditionElement;


public class QueryElementBuilder
    implements
    ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {

        final QueryElement qe = (QueryElement) rce;
        context.pushRuleComponent( qe );
        
        final int currentOffset = context.getCurrentPatternOffset();
        
        qe.getResultPattern().setOffset( currentOffset );
        
        utils.checkUnboundDeclarations( context,
                                        qe.getRequiredDeclarations() );

        context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                                                                    context.getComponentFactory().getNodeFactoryService().buildQueryElementNode(
                                                                                          context.getNextId(),
                                                                                          context.getTupleSource(),
                                                                                          qe,
                                                                                          context.isTupleMemoryEnabled(),
                                                                                          qe.isOpenQuery(),
                                                                                          context ) ) );
        context.popRuleComponent();
        context.incrementCurrentPatternOffset();
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
