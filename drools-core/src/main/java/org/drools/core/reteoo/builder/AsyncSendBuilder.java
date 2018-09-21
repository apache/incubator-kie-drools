/*
 * Copyright 2005 JBoss Inc
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
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.rule.AsyncSend;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.AlphaNodeFieldConstraint;

public class AsyncSendBuilder implements ReteooComponentBuilder {

    @Override
    public void build( BuildContext context, BuildUtils utils, RuleConditionElement rce ) {
        final AsyncSend send = (AsyncSend) rce;
        context.pushRuleComponent( send );

        @SuppressWarnings("unchecked")
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, context.getBetaconstraints(), true );

        AlphaNodeFieldConstraint[] alphaNodeFieldConstraints = context.getAlphaConstraints() != null ?
                context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ) :
                new AlphaNodeFieldConstraint[0];

        NodeFactory nodeFactory = context.getComponentFactory().getNodeFactoryService();
        AsyncSendNode sendNode =
                nodeFactory.buildAsyncSendNode( context.getNextId(),
                                                send.getDataProvider(),
                                                context.getTupleSource(),
                                                alphaNodeFieldConstraints,
                                                betaConstraints,
                                                context.isTupleMemoryEnabled(),
                                                context,
                                                send );

        context.setTupleSource( utils.attachNode( context, sendNode ) );
        context.setAlphaConstraints( null );
        context.setBetaconstraints( null );
        context.popRuleComponent();
    }

    @Override
    public boolean requiresLeftActivation( BuildUtils utils, RuleConditionElement rce ) {
        return true;
    }
}
