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

package org.drools.reteoo.builder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.common.InstanceNotEqualsConstraint;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.conf.EventProcessingOption;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.PropagationQueuingNode;
import org.drools.rule.Behavior;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Pattern;
import org.drools.rule.PatternSource;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.VariableConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.ObjectType;
import org.drools.time.impl.CompositeMaxDurationTimer;
import org.drools.time.impl.DurationTimer;
import org.drools.time.impl.Timer;

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

        context.pushRuleComponent( pattern );
        this.attachPattern( context,
                            utils,
                            pattern );
        context.popRuleComponent();

    }

    private void attachPattern(final BuildContext context,
                               final BuildUtils utils,
                               final Pattern pattern) throws InvalidPatternException {

        // Set pattern offset to the appropriate value
        pattern.setOffset( context.getCurrentPatternOffset() );

        final List<Constraint> alphaConstraints = new LinkedList<Constraint>();
        final List<Constraint> betaConstraints = new LinkedList<Constraint>();
        final List<Behavior> behaviors = new LinkedList<Behavior>();

        this.createConstraints( context,
                                utils,
                                pattern,
                                alphaConstraints,
                                betaConstraints );

        // Create BetaConstraints object
        context.setBetaconstraints( betaConstraints );

        // set behaviors list
        behaviors.addAll( pattern.getBehaviors() );
        context.setBehaviors( behaviors );

        if ( pattern.getSource() != null ) {
            context.setAlphaConstraints( alphaConstraints );
            final int currentOffset = context.getCurrentPatternOffset();

            PatternSource source = pattern.getSource();

            ReteooComponentBuilder builder = utils.getBuilderFor( source );

            builder.build( context,
                           utils,
                           source );
            // restoring offset
            context.setCurrentPatternOffset( currentOffset );

        }

        if ( pattern.getSource() == null || context.getCurrentEntryPoint() != EntryPoint.DEFAULT ) {
            attachAlphaNodes( context,
                              utils,
                              pattern,
                              alphaConstraints );

            if ( context.getCurrentEntryPoint() != EntryPoint.DEFAULT ) {
                context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                          new PropagationQueuingNode( context.getNextId(),
                                                                                                      context.getObjectSource(),
                                                                                                      context ) ) );
                // the entry-point specific network nodes are attached, so, set context to default entry-point 
                context.setCurrentEntryPoint( EntryPoint.DEFAULT );
            }
        }

        // last thing to do is increment the offset, since if the pattern has a source,
        // offset must be overriden
        context.incrementCurrentPatternOffset();
    }

    private void createConstraints(BuildContext context,
                                   BuildUtils utils,
                                   Pattern pattern,
                                   List<Constraint> alphaConstraints,
                                   List<Constraint> betaConstraints) {

        final List< ? > constraints = pattern.getConstraints();

        // check if cross products for identity patterns should be disabled
        checkRemoveIdentities( context,
                               pattern,
                               betaConstraints );

        // checks if this pattern is nested inside a NOT CE
        final boolean isNegative = isNegative( context );

        for ( final Iterator< ? > it = constraints.iterator(); it.hasNext(); ) {
            final Object object = it.next();
            // Check if its a declaration
            if ( object instanceof Declaration ) {
                // nothing to be done
                continue;
            }

            final Constraint constraint = (Constraint) object;
            if ( constraint.getType().equals( Constraint.ConstraintType.ALPHA ) ) {
                alphaConstraints.add( constraint );
            } else if ( constraint.getType().equals( Constraint.ConstraintType.BETA ) ) {
                betaConstraints.add( constraint );
                if ( isNegative && context.getRuleBase().getConfiguration().getEventProcessingMode() == EventProcessingOption.STREAM && pattern.getObjectType().isEvent() && constraint.isTemporal() ) {
                    checkDelaying( context,
                                   constraint );
                }
            } else {
                throw new RuntimeDroolsException( "Unknown constraint type: " + constraint.getType() + ". This is a bug. Please contact development team." );
            }
        }
    }

    private void checkDelaying(final BuildContext context,
                               final Constraint constraint) {
        if ( constraint instanceof VariableConstraint ) {
            // variable constraints always require a single declaration
            Declaration target = constraint.getRequiredDeclarations()[0];
            if ( target.isPatternDeclaration() && target.getPattern().getObjectType().isEvent() ) {
                long uplimit = ((VariableConstraint) constraint).getInterval().getUpperBound();

                Timer timer = context.getRule().getTimer();
                DurationTimer durationTimer = new DurationTimer( uplimit );

                if ( timer instanceof CompositeMaxDurationTimer ) {
                    // already a composite so just add
                    ((CompositeMaxDurationTimer) timer).addDurationTimer( durationTimer );
                } else {
                    if ( timer == null ) {
                        // no timer exists, so ok on it's own
                        timer = durationTimer;
                    } else {
                        // timer exists so we need to make a composite
                        CompositeMaxDurationTimer temp = new CompositeMaxDurationTimer();
                        if ( timer instanceof DurationTimer ) {
                            // previous timer was a duration, so add another DurationTimer
                            temp.addDurationTimer( (DurationTimer) timer );
                        } else {
                            // previous timer was not a duration, so set it as the delegate Timer.
                            temp.setTimer( context.getRule().getTimer() );
                        }
                        // now add the new durationTimer
                        temp.addDurationTimer( durationTimer );
                        timer = temp;
                    }
                    // with the composite made, reset it on the Rule
                    context.getRule().setTimer( timer );
                }
            }
        }
    }

    private boolean isNegative(final BuildContext context) {
        for ( ListIterator<RuleConditionElement> it = context.stackIterator(); it.hasPrevious(); ) {
            RuleConditionElement rce = it.previous();
            if ( rce instanceof GroupElement && ((GroupElement) rce).isNot() ) {
                return true;
            }
        }
        return false;
    }

    public static ObjectTypeNode attachObjectTypeNode(BuildContext context,
                                                      ObjectType objectType) {
        final InternalRuleBase ruleBase = context.getRuleBase();
        ruleBase.readLock();
        try {
            InternalWorkingMemory[] wms = context.getWorkingMemories();

            EntryPointNode epn = ruleBase.getRete().getEntryPointNode( context.getCurrentEntryPoint() );
            if ( epn == null ) {
                epn = new EntryPointNode( context.getNextId(),
                                          ruleBase.getRete(),
                                          context );
                if ( wms.length > 0 ) {
                    epn.attach( wms );
                } else {
                    epn.attach();
                }
            }

            ObjectTypeNode otn = new ObjectTypeNode( context.getNextId(),
                                                     epn,
                                                     objectType,
                                                     context );

            long expirationOffset = getExpiratioOffsetForType( context,
                                                               objectType );
            otn.setExpirationOffset( expirationOffset );

            if ( wms.length > 0 ) {
                otn.attach( wms );
            } else {
                otn.attach();
            }

            return otn;
        } finally {
            ruleBase.readUnlock();
        }
    }

    private static long getExpiratioOffsetForType(BuildContext context,
                                                  ObjectType objectType) {
        long expirationOffset = -1;
        for ( TypeDeclaration type : context.getRuleBase().getTypeDeclarations() ) {
            if ( type.getObjectType().isAssignableFrom( objectType ) ) {
                expirationOffset = Math.max( type.getExpirationOffset(),
                                             expirationOffset );
            }
        }
        // if none of the type declarations have an @expires annotation
        // we return -1 (no-expiration) value, otherwise we return the
        // set expiration value+1 to enable the fact to match events with
        // the same timestamp
        return expirationOffset == -1 ? -1 : expirationOffset+1;
    }

    public void attachAlphaNodes(final BuildContext context,
                                 final BuildUtils utils,
                                 final Pattern pattern,
                                 List<Constraint> alphaConstraints) throws InvalidPatternException {

        // Drools Query ObjectTypeNode never has memory, but other ObjectTypeNode/AlphaNoesNodes may (if not in sequential), 
        //so need to preserve, so we can restore after this node is added. LeftMemory  and Terminal remain the same once set.

        boolean objectMemory = context.isObjectTypeNodeMemoryEnabled();
        boolean alphaMemory = context.isAlphaMemoryAllowed();

        ObjectType objectType = pattern.getObjectType();
        if ( pattern.getObjectType() instanceof ClassObjectType ) {
            // Is this the query node, if so we don't want any memory
            if ( DroolsQuery.class == ((ClassObjectType) pattern.getObjectType()).getClassType() ) {
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
                context.setTerminalNodeMemoryEnabled( false );
                context.setAlphaNodeMemoryAllowed( false );
            }
        }

        context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                  new EntryPointNode( context.getNextId(),
                                                                                      context.getRuleBase().getRete(),
                                                                                      context ) ) );

        ObjectTypeNode otn = new ObjectTypeNode( context.getNextId(),
                                                 (EntryPointNode) context.getObjectSource(),
                                                 objectType,
                                                 context );
        if ( objectType.isEvent() && EventProcessingOption.STREAM.equals( context.getRuleBase().getConfiguration().getEventProcessingMode() ) ) {
            long expirationOffset = getExpiratioOffsetForType( context,
                                                               objectType );
            if( expirationOffset != -1 ) {
                // expiration policy is set, so use it
                otn.setExpirationOffset( expirationOffset );
            } else {
                // otherwise calculate it based on behaviours and temporal constraints
                for ( Behavior behavior : pattern.getBehaviors() ) {
                    if ( behavior.getExpirationOffset() != -1 ) {
                        expirationOffset = Math.max( behavior.getExpirationOffset(),
                                                     expirationOffset );
                    }
                }
                long distance = context.getTemporalDistance().getExpirationOffset( pattern );
                if( distance == -1 ) {
                    // it means the rules have no temporal constraints, or
                    // the constraints require events to be hold forever. In this 
                    // case, we allow type declarations to override the implicit 
                    // expiration offset by defining an expiration policy with the
                    // @expires tag
                    otn.setExpirationOffset( expirationOffset );
                } else {
                    otn.setExpirationOffset( Math.max( distance, expirationOffset ) );
                }
            }
        }

        context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                  otn ) );

        for ( final Iterator<Constraint> it = alphaConstraints.iterator(); it.hasNext(); ) {
            final AlphaNodeFieldConstraint constraint = (AlphaNodeFieldConstraint) it.next();

            context.pushRuleComponent( constraint );
            context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                      new AlphaNode( context.getNextId(),
                                                                                     (AlphaNodeFieldConstraint) constraint,
                                                                                     context.getObjectSource(),
                                                                                     context ) ) );
            context.popRuleComponent();
        }

        // now restore back to original values
        context.setObjectTypeNodeMemoryEnabled( objectMemory );
        context.setAlphaNodeMemoryAllowed( alphaMemory );

    }

    /**
     * @param context
     * @param pattern
     * @param betaConstraints
     */
    private void checkRemoveIdentities(final BuildContext context,
                                       final Pattern pattern,
                                       final List<Constraint> betaConstraints) {
        if ( context.getRuleBase().getConfiguration().isRemoveIdentities() && pattern.getObjectType().getClass() == ClassObjectType.class ) {
            // Check if this object type exists before
            // If it does we need stop instance equals cross product
            final Class< ? > thisClass = ((ClassObjectType) pattern.getObjectType()).getClassType();
            for ( final Iterator<Pattern> it = context.getObjectType().iterator(); it.hasNext(); ) {
                final Pattern previousPattern = it.next();
                final Class< ? > previousClass = ((ClassObjectType) previousPattern.getObjectType()).getClassType();
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
        return ((Pattern) rce).getSource() != null;
    }
}
