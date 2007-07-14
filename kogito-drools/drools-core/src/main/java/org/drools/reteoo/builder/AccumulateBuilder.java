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
import org.drools.common.TupleStartEqualsConstraint;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.RightInputAdapterNode;
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
        boolean existSubNetwort = false;

        final List resultBetaConstraints = context.getBetaconstraints();
        final List resultAlphaConstraints = context.getAlphaConstraints();

        final Pattern sourcePattern = accumulate.getSourcePattern();

        // get builder for the pattern
        final ReteooComponentBuilder builder = utils.getBuilderFor( sourcePattern );

        // save tuple source and current pattern offset for later if needed
        final TupleSource tupleSource = context.getTupleSource();
        final int currentPatternIndex = context.getCurrentPatternOffset();
        
        // builds the source pattern
        builder.build( context,
                       utils,
                       sourcePattern );

        // if object source is null, then we need to adapt tuple source into a subnetwork
        if ( context.getObjectSource() == null ) {

            // attach right input adapter node to convert tuple source into an object source
            context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                      new RightInputAdapterNode( context.getNextId(),
                                                                                                 context.getTupleSource() ) ) );

            // restore tuple source from before the start of the sub network
            context.setTupleSource( tupleSource );

            // create a tuple start equals constraint and set it in the context
            final TupleStartEqualsConstraint constraint = TupleStartEqualsConstraint.getInstance();
            final List betaConstraints = new ArrayList();
            betaConstraints.add( constraint );
            context.setBetaconstraints( betaConstraints );
            existSubNetwort = true;
        }
        
        final BetaConstraints resultsBinder = utils.createBetaNodeConstraint( context,
                                                                              resultBetaConstraints,
                                                                              false );
        final BetaConstraints sourceBinder = utils.createBetaNodeConstraint( context,
                                                                             context.getBetaconstraints(),
                                                                             false );

        context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                new AccumulateNode( context.getNextId(),
                                                                                    context.getTupleSource(),
                                                                                    context.getObjectSource(),
                                                                                    (AlphaNodeFieldConstraint[]) resultAlphaConstraints.toArray( new AlphaNodeFieldConstraint[resultAlphaConstraints.size()] ),
                                                                                    sourceBinder,
                                                                                    resultsBinder,
                                                                                    accumulate,
                                                                                    existSubNetwort ) ) );
        // source pattern was bound, so nulling context
        context.setObjectSource( null );
        context.setCurrentPatternOffset( currentPatternIndex );
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
