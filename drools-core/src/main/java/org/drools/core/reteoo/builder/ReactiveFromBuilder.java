/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.core.common.BaseNode;
import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.rule.From;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.rule.constraint.XpathConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;

import java.util.List;

public class ReactiveFromBuilder implements ReteooComponentBuilder {

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

        BaseNode node = context.getComponentFactory().getNodeFactoryService()
                               .buildReactiveFromNode(context.getNextId(),
                                                      from.getDataProvider(),
                                                      context.getTupleSource(),
                                                      alphaNodeFieldConstraints,
                                                      betaConstraints,
                                                      context.isTupleMemoryEnabled(),
                                                      context,
                                                      from);

        context.setTupleSource( (LeftTupleSource) utils.attachNode( context, node ) );
        context.setAlphaConstraints(null);
        context.setBetaconstraints( null );

        context.incrementCurrentPatternOffset();
        int patternOffset = context.getCurrentPatternOffset();

        List<XpathConstraint> xpathConstraints = context.getXpathConstraints();
        for (XpathConstraint xpathConstraint : xpathConstraints) {
            for ( XpathConstraint.XpathChunk chunk : xpathConstraint.getChunks() ) {
                context.setAlphaConstraints( chunk.getAlphaConstraints() );
                context.setBetaconstraints( chunk.getBetaConstraints() );
                context.setXpathConstraints( chunk.getXpathConstraints() );
                build( context, utils, chunk.asFrom() );
            }
        }

        context.setCurrentPatternOffset( patternOffset );
    }

    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
