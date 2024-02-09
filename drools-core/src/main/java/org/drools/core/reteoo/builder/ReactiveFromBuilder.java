/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo.builder;

import java.util.List;

import org.drools.base.rule.From;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.constraint.XpathConstraint;
import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.ReactiveFromNode;

public class ReactiveFromBuilder implements ReteooComponentBuilder {

    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final From from = (From) rce;
        context.pushRuleComponent( from );

        @SuppressWarnings("unchecked")
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, context.getBetaconstraints(), true );

        AlphaNodeFieldConstraint[] alphaNodeFieldConstraints = context.getAlphaConstraints() != null ?
                                                               context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ) :
                                                               new AlphaNodeFieldConstraint[0];

        ReactiveFromNode node = CoreComponentFactory.get().getNodeFactoryService()
                                       .buildReactiveFromNode(context.getNextNodeId(),
                                                              from.getDataProvider(),
                                                              context.getTupleSource(),
                                                              alphaNodeFieldConstraints,
                                                              betaConstraints,
                                                              context.isTupleMemoryEnabled(),
                                                              context,
                                                              from);

        context.setTupleSource( utils.attachNode( context, node ) );
        context.setAlphaConstraints(null);
        context.setBetaconstraints( null );

        List<XpathConstraint> xpathConstraints = context.getXpathConstraints();
        for (XpathConstraint xpathConstraint : xpathConstraints) {
            for ( XpathConstraint.XpathChunk chunk : xpathConstraint.getChunks() ) {
                context.setAlphaConstraints( chunk.getAlphaConstraints() );
                context.setBetaconstraints( chunk.getBetaConstraints() );
                context.setXpathConstraints( chunk.getXpathConstraints() );
                build( context, utils, chunk.asFrom() );
            }
        }
    }

    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
