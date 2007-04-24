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

package org.drools.reteoo.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.common.BetaConstraints;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.TupleSource;
import org.drools.rule.Accumulate;
import org.drools.rule.Pattern;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.AlphaNodeFieldConstraint;

/**
 * @author etirelli
 *
 */
public class AccumulateBuilder
    implements
    ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final Accumulate accumulate = (Accumulate) rce;

        final Pattern sourcePattern = accumulate.getSourcePattern();

        // get builder for the pattern
        final ReteooComponentBuilder builder = utils.getBuilderFor( sourcePattern );

        // builds the source pattern
        builder.build( context,
                       utils,
                       sourcePattern );

        final Pattern pattern = accumulate.getResultPattern();
        // adjusting target pattern offset to be the same as the source pattern
        pattern.setOffset( context.getCurrentPatternOffset() - 1 );

        final List constraints = pattern.getConstraints();

        final List betaConstraints = new ArrayList();
        final List alphaConstraints = new ArrayList();

        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                continue;
            }

            final AlphaNodeFieldConstraint fieldConstraint = (AlphaNodeFieldConstraint) object;
            if ( fieldConstraint instanceof LiteralConstraint ) {
                alphaConstraints.add( fieldConstraint );
            } else {
                utils.checkUnboundDeclarations( context,
                                                fieldConstraint.getRequiredDeclarations() );
                betaConstraints.add( fieldConstraint );
            }
        }

        final BetaConstraints resultsBinder = utils.createBetaNodeConstraint( context,
                                                                              betaConstraints );

        context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                new AccumulateNode( context.getNextId(),
                                                                                    context.getTupleSource(),
                                                                                    context.getObjectSource(),
                                                                                    (AlphaNodeFieldConstraint[]) alphaConstraints.toArray( new AlphaNodeFieldConstraint[alphaConstraints.size()] ),
                                                                                    context.getBetaconstraints(),
                                                                                    resultsBinder,
                                                                                    accumulate ) ) );
        // source pattern was bound, so nulling context
        context.setObjectSource( null );
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
