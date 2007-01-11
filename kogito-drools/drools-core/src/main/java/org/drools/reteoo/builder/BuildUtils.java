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

import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DoubleBetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.QuadroupleBetaConstraints;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.TupleSource;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.BetaNodeFieldConstraint;

/**
 * Utility functions for reteoo build
 * 
 * @author etirelli
 */
public class BuildUtils {

    private Map componentBuilders = new HashMap();

    /**
     * Adds the given builder for the given target to the builders map
     * 
     * @param target
     * @param builder
     */
    public void addBuilder(Class target,
                           ReteooComponentBuilder builder) {
        this.componentBuilders.put( target, builder );
    }
    
    /**
     * Returns a builder for the given target from the builders map
     * 
     * @param target
     * @return returns null if not found
     */
    public ReteooComponentBuilder getBuilderFor( RuleConditionElement target ) {
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
     *         or eventualy one that was already in the cache if sharing is enabled
     */
    public BaseNode attachNode(final BuildContext context,
                               final BaseNode candidate) {
        // checks if node is in the cache
        BaseNode node = (BaseNode) context.getNodeFromCache( candidate );

        // if node sharing is enabled and node was found in the cache
        if ( (node != null) && isSharingEnabledForNode( context,
                                                        node ) ) {
            // if node was not previously in use, attach it
            if ( !node.isInUse() ) {
                if ( context.getWorkingMemories().length == 0 ) {
                    node.attach();
                } else {
                    node.attach( context.getWorkingMemories() );
                }
            }
            // increment share counter
            node.addShare();
            // undo previous id assignment
            context.releaseLastId();
        } else {
            // attach candidate node
            if ( context.getWorkingMemories().length == 0 ) {
                candidate.attach();
            } else {
                candidate.attach( context.getWorkingMemories() );
            }

            // add it to cache
            context.addNodeToCache( candidate );

            node = candidate;
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
    private boolean isSharingEnabledForNode(BuildContext context,
                                            BaseNode node) {
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
                                                    final List list) {
        BetaConstraints constraints;
        switch ( list.size() ) {
            case 0 :
                constraints = EmptyBetaConstraints.getInstance();
                break;
            case 1 :
                constraints = new SingleBetaConstraints( (BetaNodeFieldConstraint) list.get( 0 ), context.getRuleBase().getConfiguration() );
                break;
            case 2 :
                constraints = new DoubleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), context.getRuleBase().getConfiguration() );
                break;
            case 3 :
                constraints = new TripleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), context.getRuleBase().getConfiguration() );
                break;
            case 4 :
                constraints = new QuadroupleBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), context.getRuleBase().getConfiguration() );
                break;                
            default :
                constraints = new DefaultBetaConstraints( (BetaNodeFieldConstraint[]) list.toArray( new BetaNodeFieldConstraint[list.size()] ), context.getRuleBase().getConfiguration() );
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
            for( ListIterator it = context.stackIterator(); it.hasPrevious(); ) {
                RuleConditionElement rce = (RuleConditionElement) it.previous();
                if( rce.resolveDeclaration( declarations[i].getIdentifier() ) == null ) {
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
