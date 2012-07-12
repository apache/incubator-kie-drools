/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.builder;

import java.util.List;

import org.drools.common.BetaConstraints;
import org.drools.reteoo.FromNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.rule.From;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;

public class FromBuilder
    implements
    ReteooComponentBuilder {

    /* (non-Javadoc)
     * @see org.drools.reteoo.builder.ReteooComponentBuilder#build(org.drools.reteoo.builder.BuildContext, org.drools.reteoo.builder.BuildUtils, org.drools.rule.RuleConditionElement)
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final From from = (From) rce;
        context.pushRuleComponent( from );

        @SuppressWarnings("unchecked")
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, (List<BetaNodeFieldConstraint>) context.getBetaconstraints(), true );
        
        context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                ReteooComponentFactory.getNodeFactoryService().buildFromNode( context.getNextId(),
                                                                              from.getDataProvider(),
                                                                              context.getTupleSource(),
                                                                              (AlphaNodeFieldConstraint[]) context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ),
                                                                              betaConstraints,
                                                                              context.isTupleMemoryEnabled(),
                                                                              context,
                                                                              from ) ) );
        context.setAlphaConstraints( null );
        context.setBetaconstraints( null );
        context.popRuleComponent();
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
