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
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.common.BetaConstraints;
import org.drools.common.InstanceNotEqualsConstraint;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.Pattern;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;

/**
 * A builder for patterns
 * 
 * @author etirelli
 */
public class PatternBuilder
    implements
    ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {

        final Pattern pattern = (Pattern) rce;

        context.setBetaconstraints( this.attachPattern( context,
                                                       utils,
                                                       pattern ) );

    }

    private BetaConstraints attachPattern(final BuildContext context,
                                         final BuildUtils utils,
                                         final Pattern pattern) throws InvalidPatternException {

        // Set pattern offset to the appropriate value
        pattern.setOffset( context.getCurrentPatternOffset() );

        context.incrementCurrentPatternOffset();

        // Attach alpha nodes
        final List predicates = attachAlphaNodes( context,
                                                  utils,
                                                  pattern );

        // Create BetaConstraints object
        final BetaConstraints binder = utils.createBetaNodeConstraint( context,
                                                                       predicates );

        return binder;
    }

    public List attachAlphaNodes(final BuildContext context,
                                 final BuildUtils utils,
                                 final Pattern pattern) throws InvalidPatternException {

        final List constraints = pattern.getConstraints();

        context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                  new ObjectTypeNode( context.getNextId(),
                                                                                      pattern.getObjectType(),
                                                                                      context.getRuleBase().getRete(),
                                                                                      context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() ) ) );

        final List betaConstraints = new ArrayList();

        // check if cross products for identity patterns should be disabled
        checkRemoveIdentities( context,
                               pattern,
                               betaConstraints );

        for ( final Iterator it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                // nothing to be done
                continue;
            }

            final Constraint constraint = (Constraint) object;
            final Declaration[] declarations = constraint.getRequiredDeclarations();

            boolean isAlphaConstraint = true;
            for ( int i = 0; isAlphaConstraint && i < declarations.length; i++ ) {
                if ( declarations[i].getPattern() != pattern ) {
                    isAlphaConstraint = false;
                }
            }
            if ( isAlphaConstraint ) {
                context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                          new AlphaNode( context.getNextId(),
                                                                                         (AlphaNodeFieldConstraint) constraint,
                                                                                         context.getObjectSource() ) ) );
            } else {
                utils.checkUnboundDeclarations( context,
                                                constraint.getRequiredDeclarations() );
                betaConstraints.add( constraint );
            }
        }

        return betaConstraints;
    }

    /**
     * @param context
     * @param pattern
     * @param betaConstraints
     */
    private void checkRemoveIdentities(final BuildContext context,
                                       final Pattern pattern,
                                       final List betaConstraints) {
        if ( context.getRuleBase().getConfiguration().isRemoveIdentities() && pattern.getObjectType().getClass() == ClassObjectType.class ) {
            List patterns = null;
            // Check if this object type exists before
            // If it does we need stop instance equals cross product
            final Class thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
            for ( final Iterator it = context.getObjectType().entrySet().iterator(); it.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) it.next();
                final Class previousClass = ((ClassObjectType) entry.getKey()).getClassType();
                if ( thisClass.isAssignableFrom( previousClass ) ) {
                    patterns = (List) entry.getValue();
                    for ( final Iterator patternIter = patterns.iterator(); patternIter.hasNext(); ) {
                        betaConstraints.add( new InstanceNotEqualsConstraint( (Pattern) patternIter.next() ) );
                    }
                }
            }
            patterns = (List) context.getObjectType().get( pattern.getObjectType() );
            if ( patterns == null ) {
                patterns = new ArrayList();
            }
            patterns.add( pattern );

            // Must be added after the checking, otherwise it matches against itself
            context.getObjectType().put( pattern.getObjectType(),
                                         patterns );
        }
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return false;
    }
}
