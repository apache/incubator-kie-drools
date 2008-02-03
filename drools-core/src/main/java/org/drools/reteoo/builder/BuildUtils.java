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
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.TupleSink;
import org.drools.reteoo.TupleSource;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.ObjectType;

/**
 * Utility functions for reteoo build
 * 
 * @author etirelli
 */
public class BuildUtils {

    private final Map componentBuilders = new HashMap();

    /**
     * Adds the given builder for the given target to the builders map
     * 
     * @param target
     * @param builder
     */
    public void addBuilder(final Class target,
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
        return (ReteooComponentBuilder) this.componentBuilders.get( target.getClass() );
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
        if( candidate instanceof EntryPointNode ) {
            // entry point nodes are always shared
            EntryPointNode epn = context.getRuleBase().getRete().getEntryPointNode( ((EntryPointNode)candidate).getEntryPoint() );
            if( epn != null ) {
                node = epn;
            }
        } else if( candidate instanceof ObjectTypeNode ) {
            // object type nodes are always shared
            ObjectTypeNode otn = (ObjectTypeNode) candidate;
            Map<ObjectType, ObjectTypeNode> map = context.getRuleBase().getRete().getObjectTypeNodes( context.getCurrentEntryPoint() );
            if( map != null ) {
                otn = map.get( otn.getObjectType() );
                if ( otn != null ) {
                    node = otn;
                }
            }
        } else if( isSharingEnabledForNode( context, candidate ) ) {
            if ( (context.getTupleSource() != null) && ( candidate instanceof TupleSink ) ) {
                TupleSink[] sinks = context.getTupleSource().getSinkPropagator().getSinks(); 
                for( int i = 0; i < sinks.length; i++ ) {
                    if( candidate.equals( sinks[i] ) ) {
                        node = (BaseNode) sinks[i];
                        break;
                    }
                }
            } else if ( (context.getObjectSource() != null) && (candidate instanceof ObjectSink) ) {
                ObjectSink[] sinks = context.getObjectSource().getSinkPropagator().getSinks();
                for( int i = 0; i < sinks.length; i++ ) {
                    if( candidate.equals( sinks[i] ) ) {
                        node = (BaseNode) sinks[i];
                        break;
                    }
                }
            } else {
                throw new RuntimeDroolsException( "This is a bug on node sharing verification. Please report to development team." );
            }
            if( node != null ) {
                // shared node found
                // undo previous id assignment
                context.releaseId( candidate.getId() );
                node.addShare();
            }
        }
        

        if ( node == null ) {
            // only attach() if it is a new node
            node = candidate;
            if ( context.getWorkingMemories().length == 0 ) {
                node.attach();
            } else {
                node.attach( context.getWorkingMemories() );
            }
        }
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
        if ( node instanceof TupleSource ) {
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
                                                    final List list,
                                                    final boolean disableIndexing ) {
        BetaConstraints constraints;
        switch ( list.size() ) {
            case 0 :
                constraints = EmptyBetaConstraints.getInstance();
                break;
            case 1 :
                constraints = new SingleBetaConstraints( (BetaNodeFieldConstraint) list.get( 0 ),
                                                         context.getRuleBase().getConfiguration(),
                                                         disableIndexing );
                break;
            case 2 :
                constraints = new DoubleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                         context.getRuleBase().getConfiguration(),
                                                         disableIndexing  );
                break;
            case 3 :
                constraints = new TripleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                         context.getRuleBase().getConfiguration(),
                                                         disableIndexing  );
                break;
            case 4 :
                constraints = new QuadroupleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                             context.getRuleBase().getConfiguration(),
                                                             disableIndexing  );
                break;
            default :
                constraints = new DefaultBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ),
                                                          context.getRuleBase().getConfiguration(),
                                                          disableIndexing  );
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
        final List list = new ArrayList();
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            for ( final ListIterator it = context.stackIterator(); it.hasPrevious(); ) {
                final RuleConditionElement rce = (RuleConditionElement) it.previous();
                final Declaration decl = rce.resolveDeclaration( declarations[i].getIdentifier() );
                if ( decl == null || decl.getPattern().getOffset() > declarations[i].getPattern().getOffset() ) {
                    list.add( declarations[i].getIdentifier() );
                }
            }
        }

        // Make sure the required declarations        
        if ( list.size() != 0 ) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append( list.get( 0 ) );
            for ( int i = 1, size = list.size(); i < size; i++ ) {
                buffer.append( ", " + list.get( i ) );
            }

            throw new InvalidPatternException( "Required Declarations not bound: '" + buffer );
        }
    }

}
