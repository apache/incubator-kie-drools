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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DoubleBetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.QuadroupleBetaConstraints;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.rule.AbstractCompositeConstraint;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.ObjectType;
import org.drools.time.Interval;
import org.drools.time.TemporalDependencyMatrix;
import org.drools.time.TimeUtils;

/**
 * Utility functions for reteoo build
 *
 */
public class BuildUtils {

    private final Map<Class< ? >, ReteooComponentBuilder> componentBuilders = new HashMap<Class< ? >, ReteooComponentBuilder>();

    /**
     * Adds the given builder for the given target to the builders map
     *
     * @param target
     * @param builder
     */
    public void addBuilder(final Class< ? > target,
                           final ReteooComponentBuilder builder) {
        this.componentBuilders.put( target,
                                    builder );
    }

    /**
     * Returns a builder for the given target from the builders map
     *
     * @param target
     * @return returns null if not found
     */
    public ReteooComponentBuilder getBuilderFor(final RuleConditionElement target) {
        return this.componentBuilders.get( target.getClass() );
    }

    /**
     * Attaches a node into the network. If a node already exists that could
     * substitute, it is used instead.
     *
     * @param context
     *            The current build context
     * @param candidate
     *            The node to attach.
     *
     * @return the actual attached node that may be the one given as parameter
     *         or eventually one that was already in the cache if sharing is enabled
     */
    public BaseNode attachNode(final BuildContext context,
                               final BaseNode candidate) {
        BaseNode node = null;
        RuleBasePartitionId partition = null;
        if ( candidate instanceof EntryPointNode ) {
            // entry point nodes are always shared
            node = context.getRuleBase().getRete().getEntryPointNode( ((EntryPointNode) candidate).getEntryPoint() );
            // all EntryPointNodes belong to the main partition
            partition = RuleBasePartitionId.MAIN_PARTITION;
        } else if ( candidate instanceof ObjectTypeNode ) {
            // object type nodes are always shared
            Map<ObjectType, ObjectTypeNode> map = context.getRuleBase().getRete().getObjectTypeNodes( context.getCurrentEntryPoint() );
            if ( map != null ) {
                ObjectTypeNode otn = map.get( ((ObjectTypeNode) candidate).getObjectType() );
                if ( otn != null ) {
                    // adjusting expiration offset
                    otn.setExpirationOffset( Math.max( otn.getExpirationOffset(),
                                                       ((ObjectTypeNode) candidate).getExpirationOffset() ) );
                    node = otn;
                }
            }
            // all ObjectTypeNodes belong to the main partition
            partition = RuleBasePartitionId.MAIN_PARTITION;
        } else if ( isSharingEnabledForNode( context,
                                             candidate ) ) {
            if ( (context.getTupleSource() != null) && (candidate instanceof LeftTupleSink) ) {
                node = context.getTupleSource().getSinkPropagator().getMatchingNode( candidate );
            } else if ( (context.getObjectSource() != null) && (candidate instanceof ObjectSink) ) {
                node = context.getObjectSource().getSinkPropagator().getMatchingNode( candidate );
            } else {
                throw new RuntimeDroolsException( "This is a bug on node sharing verification. Please report to development team." );
            }
        }

        if ( node == null ) {
            // only attach() if it is a new node
            node = candidate;

            // new node, so it must be labeled
            if ( partition == null ) {
                // if it does not has a predefined label
                if ( context.getPartitionId() == null ) {
                    // if no label in current context, create one
                    context.setPartitionId( context.getRuleBase().createNewPartitionId() );
                }
                partition = context.getPartitionId();
            }
            // set node whit the actual partition label
            node.setPartitionId( partition );

            if ( context.getWorkingMemories().length == 0 ) {
                node.attach();
            } else {
                node.attach( context.getWorkingMemories() );
            }
            // adds the node to the context list to track all added nodes
            context.getNodes().add( node );
        } else {
            // shared node found
            // undo previous id assignment
            context.releaseId( candidate.getId() );
        }
        node.addAssociation( context.getRule(), context.peekRuleComponent() );
        return node;
    }

    /**
     * Utility function to check if sharing is enabled for nodes of the given class
     *
     * @param context
     * @param node
     * @return
     */
    private boolean isSharingEnabledForNode(final BuildContext context,
                                            final BaseNode node) {
        if ( node instanceof LeftTupleSource ) {
            return context.getRuleBase().getConfiguration().isShareBetaNodes();
        } else if ( node instanceof ObjectSource ) {
            return context.getRuleBase().getConfiguration().isShareAlphaNodes();
        }
        return false;
    }

    /**
     * Creates and returns a BetaConstraints object for the given list of constraints
     *
     * @param context the current build context
     * @param list the list of constraints
     *
     * @return
     */
    public BetaConstraints createBetaNodeConstraint(final BuildContext context,
                                                    final List<BetaNodeFieldConstraint> list,
                                                    final boolean disableIndexing) {
        BetaConstraints constraints;
        switch ( list.size() ) {
            case 0 :
                constraints = EmptyBetaConstraints.getInstance();
                break;
            case 1 :
                constraints = new SingleBetaConstraints( list.get( 0 ),
                                                         context.getRuleBase().getConfiguration(),
                                                         disableIndexing );
                break;
            case 2 :
                constraints = new DoubleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                         context.getRuleBase().getConfiguration(),
                                                         disableIndexing );
                break;
            case 3 :
                constraints = new TripleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                         context.getRuleBase().getConfiguration(),
                                                         disableIndexing );
                break;
            case 4 :
                constraints = new QuadroupleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                             context.getRuleBase().getConfiguration(),
                                                             disableIndexing );
                break;
            default :
                constraints = new DefaultBetaConstraints( list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                          context.getRuleBase().getConfiguration(),
                                                          disableIndexing );
        }
        return constraints;
    }

    /**
     * Make sure the required declarations are previously bound
     *
     * @param declarations
     * @throws InvalidPatternException
     */
    public void checkUnboundDeclarations(final BuildContext context,
                                         final Declaration[] declarations) throws InvalidPatternException {
        final List<String> list = new ArrayList<String>();
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            boolean resolved = false;
            for ( final ListIterator<RuleConditionElement> it = context.stackIterator(); it.hasPrevious(); ) {
                final RuleConditionElement rce = it.previous();
                final Declaration decl = rce.resolveDeclaration( declarations[i].getIdentifier() );
                if ( decl != null && decl.getPattern().getOffset() <= declarations[i].getPattern().getOffset() ) {
                    resolved = true;
                    break;
                }
            }
            if( ! resolved ) {
                list.add( declarations[i].getIdentifier() );
            }
        }

        // Make sure the required declarations
        if ( list.size() != 0 ) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append( list.get( 0 ) );
            for ( int i = 1, size = list.size(); i < size; i++ ) {
                buffer.append( ", " + list.get( i ) );
            }

            throw new InvalidPatternException( "Required Declarations not bound: '" + buffer + "'");
        }
    }

    /**
     * Calculates the temporal distance between all event patterns in the given 
     * subrule.
     * 
     * @param groupElement the root element of a subrule being added to the rulebase
     * 
     */
    public TemporalDependencyMatrix calculateTemporalDistance(GroupElement groupElement) {
        // find the events
        List<Pattern> events = new ArrayList<Pattern>();
        selectAllEventPatterns( events,
                                groupElement );

        final int size = events.size();
        if ( size >= 1 ) {
            // create the matrix
            Interval[][] source = new Interval[size][];
            for ( int row = 0; row < size; row++ ) {
                source[row] = new Interval[size];
                for ( int col = 0; col < size; col++ ) {
                    if ( row == col ) {
                        source[row][col] = new Interval( 0,
                                                         0 );
                    } else {
                        source[row][col] = new Interval( Interval.MIN,
                                                         Interval.MAX );
                    }
                }
            }

            Interval[][] result;
            if ( size > 1 ) {
                List<Declaration> declarations = new ArrayList<Declaration>();
                int eventIndex = 0;
                // populate the matrix
                for ( Pattern event : events ) {
                    // references to other events are always backward references, so we can build the list as we go
                    declarations.add( event.getDeclaration() );
                    Map<Declaration, Interval> temporal = new HashMap<Declaration, Interval>();
                    gatherTemporalRelationships( event.getConstraints(),
                                                 temporal );
                    // intersect default values with the actual constrained intervals
                    for ( Map.Entry<Declaration, Interval> entry : temporal.entrySet() ) {
                        int targetIndex = declarations.indexOf( entry.getKey() );
                        Interval interval = entry.getValue();
                        source[targetIndex][eventIndex].intersect( interval );
                        Interval reverse = new Interval( interval.getUpperBound() == Long.MAX_VALUE ? Long.MIN_VALUE : -interval.getUpperBound(), 
                                                         interval.getLowerBound() == Long.MIN_VALUE ? Long.MAX_VALUE : -interval.getLowerBound() );
                        source[eventIndex][targetIndex].intersect( reverse );
                    }
                    eventIndex++;
                }
                result = TimeUtils.calculateTemporalDistance( source );
            } else {
                result = source;
            }
            TemporalDependencyMatrix matrix = new TemporalDependencyMatrix( result,
                                                                            events );
            return matrix;
        }
        return null;
    }

    private void gatherTemporalRelationships(List< ? > constraints,
                                             Map<Declaration, Interval> temporal) {
        for ( Object obj : constraints ) {
            if ( obj instanceof VariableConstraint ) {
                VariableConstraint constr = (VariableConstraint) obj;
                if ( constr.isTemporal() ) {
                    // if a constraint already exists, calculate the intersection
                    Declaration target = constr.getRequiredDeclarations()[0];
                    // only calculate relationships to other event patterns
                    if( target.isPatternDeclaration() && target.getPattern().getObjectType().isEvent() ) {
                        Interval interval = temporal.get( target );
                        if ( interval == null ) {
                            interval = constr.getInterval();
                            temporal.put( target,
                                          interval );
                        } else {
                            interval.intersect( constr.getInterval() );
                        }
                    }
                }
            } else if ( obj instanceof AbstractCompositeConstraint ) {
                gatherTemporalRelationships( Arrays.asList( ((AbstractCompositeConstraint) obj).getBetaConstraints() ),
                                             temporal );
            }
        }
    }

    private void selectAllEventPatterns(List<Pattern> events,
                                        RuleConditionElement rce) {
        if ( rce instanceof Pattern ) {
            Pattern p = (Pattern) rce;
            if ( p.getObjectType().isEvent() ) {
                events.add( p );
            }
        }
        for ( RuleConditionElement child : rce.getNestedElements() ) {
            selectAllEventPatterns( events,
                                    child );
        }
    }

}
