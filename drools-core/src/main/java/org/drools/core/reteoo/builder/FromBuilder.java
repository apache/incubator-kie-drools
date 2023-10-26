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

import org.drools.base.rule.From;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.common.BetaConstraints;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.FromNode;

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
        BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context, context.getBetaconstraints(), true );

        AlphaNodeFieldConstraint[] alphaNodeFieldConstraints = context.getAlphaConstraints() != null ?
                                                               context.getAlphaConstraints().toArray( new AlphaNodeFieldConstraint[context.getAlphaConstraints().size()] ) :
                                                               new AlphaNodeFieldConstraint[0];

        NodeFactory nodeFactory = CoreComponentFactory.get().getNodeFactoryService();
        FromNode fromNode = from.isReactive() ?
                            nodeFactory.buildReactiveFromNode( context.getNextNodeId(),
                                                               from.getDataProvider(),
                                                               context.getTupleSource(),
                                                               alphaNodeFieldConstraints,
                                                               betaConstraints,
                                                               context.isTupleMemoryEnabled(),
                                                               context,
                                                               from ) :
                            nodeFactory.buildFromNode( context.getNextNodeId(),
                                                       from.getDataProvider(),
                                                       context.getTupleSource(),
                                                       alphaNodeFieldConstraints,
                                                       betaConstraints,
                                                       context.isTupleMemoryEnabled(),
                                                       context,
                                                       from );


        context.setTupleSource( utils.attachNode( context, fromNode ) );
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
