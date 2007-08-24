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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.drools.base.ClassObjectType;
import org.drools.common.InstanceNotEqualsConstraint;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.Rete;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.ReteooBuilder.IdGenerator;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Pattern;
import org.drools.rule.PatternSource;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.ObjectType;

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

        this.attachPattern( context,
                            utils,
                            pattern );

    }

    private void attachPattern(final BuildContext context,
                               final BuildUtils utils,
                               final Pattern pattern) throws InvalidPatternException {

        // Set pattern offset to the appropriate value
        pattern.setOffset( context.getCurrentPatternOffset() );

        context.incrementCurrentPatternOffset();

        final List alphaConstraints = new LinkedList();
        final List betaConstraints = new LinkedList();

        this.createConstraints( context,
                                utils,
                                pattern,
                                alphaConstraints,
                                betaConstraints );

        // Create BetaConstraints object
        context.setBetaconstraints( betaConstraints );

        if ( pattern.getSource() == null ) {
            // pattern is selected from working memory, so 
            // Attach alpha nodes
            attachAlphaNodes( context,
                              utils,
                              pattern,
                              alphaConstraints );

        } else {
            context.setAlphaConstraints( alphaConstraints );
            
            PatternSource source = pattern.getSource();

            ReteooComponentBuilder builder = utils.getBuilderFor( source );

            builder.build( context,
                           utils,
                           source );
        }
    }

    private void createConstraints(BuildContext context,
                                   BuildUtils utils,
                                   Pattern pattern,
                                   List alphaConstraints,
                                   List betaConstraints) {

        final List constraints = pattern.getConstraints();

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
                if ( ! declarations[i].isGlobal() && declarations[i].getPattern() != pattern ) {
                    isAlphaConstraint = false;
                }
            }

            if ( isAlphaConstraint ) {
                alphaConstraints.add( constraint );
            } else {
                utils.checkUnboundDeclarations( context,
                                                constraint.getRequiredDeclarations() );
                betaConstraints.add( constraint );
            }
        }
    }
    
    public static ObjectTypeNode attachObjectTypeNode(Rete rete, ObjectType objectType) {
        ReteooRuleBase ruleBase = ( ReteooRuleBase ) rete.getRuleBase();
        ReteooBuilder builder = ruleBase.getReteooBuilder();
                
        ObjectTypeNode otn = new ObjectTypeNode( builder.getIdGenerator().getNextId(),
                            objectType,
                            rete,
                            ruleBase.getConfiguration().getAlphaNodeHashingThreshold() );
                
        InternalWorkingMemory[] wms = ruleBase.getWorkingMemories();
        otn.attach( wms );      
        
        return otn;
    }

    public void attachAlphaNodes(final BuildContext context,
                                 final BuildUtils utils,
                                 final Pattern pattern,
                                 List alphaConstraints) throws InvalidPatternException {

        context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                  new ObjectTypeNode( context.getNextId(),
                                                                                      pattern.getObjectType(),
                                                                                      context.getRuleBase().getRete(),
                                                                                      context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() ) ) );

        for ( final Iterator it = alphaConstraints.iterator(); it.hasNext(); ) {
            final AlphaNodeFieldConstraint constraint = (AlphaNodeFieldConstraint) it.next();

            context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                      new AlphaNode( context.getNextId(),
                                                                                     (AlphaNodeFieldConstraint) constraint,
                                                                                     context.getObjectSource(),
                                                                                     context.getRuleBase().getConfiguration().isAlphaMemory(),
                                                                                     context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() ) ) );
        }

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
            for ( final Iterator it = context.getObjectType().iterator(); it.hasNext(); ) {
                final Pattern previousPattern = (Pattern) it.next();
                final Class previousClass = ((ClassObjectType) previousPattern.getObjectType()).getClassType();
                if ( thisClass.isAssignableFrom( previousClass ) ) {
                    betaConstraints.add( new InstanceNotEqualsConstraint( previousPattern ) );
                }
            }

            // Must be added after the checking, otherwise it matches against itself
            context.getObjectType().add( pattern );
        }
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return ((Pattern)rce).getSource() != null;
    }
}
