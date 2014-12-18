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

package org.drools.core.reteoo.builder;

import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.rule.From;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;

import java.util.List;

public class FromBuilder
    implements
    ReteooComponentBuilder {

    /* (non-Javadoc)
     * @see org.kie.reteoo.builder.ReteooComponentBuilder#build(org.kie.reteoo.builder.BuildContext, org.kie.reteoo.builder.BuildUtils, org.kie.rule.RuleConditionElement)
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final From from = (From) rce;
        context.pushRuleComponent( from );

        @SuppressWarnings("unchecked")
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, (List<BetaNodeFieldConstraint>) context.getBetaconstraints(), true );

        AlphaNodeFieldConstraint[] alphaNodeFieldConstraints = context.getAlphaConstraints() != null ?
                                                               context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ) :
                                                               new AlphaNodeFieldConstraint[0];

        context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                context.getComponentFactory().getNodeFactoryService().buildFromNode( context.getNextId(),
                                                                                     from.getDataProvider(),
                                                                                     context.getTupleSource(),
                                                                                     alphaNodeFieldConstraints,
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
