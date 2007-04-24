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
import org.drools.reteoo.FromNode;
import org.drools.reteoo.TupleSource;
import org.drools.rule.Pattern;
import org.drools.rule.Declaration;
import org.drools.rule.From;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.AlphaNodeFieldConstraint;

/**
 * @author etirelli
 *
 */
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

        final Pattern pattern = from.getPattern();

        // setting and incrementing pattern offset as appropriate
        pattern.setOffset( context.getCurrentPatternOffset() );
        context.incrementCurrentPatternOffset();

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

        final BetaConstraints binder = utils.createBetaNodeConstraint( context,
                                                                       betaConstraints );

        context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                new FromNode( context.getNextId(),
                                                                              from.getDataProvider(),
                                                                              context.getTupleSource(),
                                                                              (AlphaNodeFieldConstraint[]) alphaConstraints.toArray( new AlphaNodeFieldConstraint[alphaConstraints.size()] ),
                                                                              binder ) ) );
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
