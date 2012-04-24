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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.common.BetaConstraints;
import org.drools.common.TupleStartEqualsConstraint;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.QueryRiaFixerNode;
import org.drools.reteoo.RightInputAdapterNode;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElement.Type;
import org.drools.rule.RuleConditionElement;
import org.drools.spi.BetaNodeFieldConstraint;

public class GroupElementBuilder
    implements
    ReteooComponentBuilder {

    protected final Map<Type, ReteooComponentBuilder> geBuilders = new HashMap<Type, ReteooComponentBuilder>();

    public GroupElementBuilder() {
        this.geBuilders.put( GroupElement.AND,
                             new AndBuilder() );
        this.geBuilders.put( GroupElement.OR,
                             new OrBuilder() );
        this.geBuilders.put( GroupElement.NOT,
                             new NotBuilder() );
        this.geBuilders.put( GroupElement.EXISTS,
                             new ExistsBuilder() );
    }

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final GroupElement ge = (GroupElement) rce;

        final ReteooComponentBuilder builder = this.geBuilders.get( ge.getType() );
        
        context.push( ge );
        context.pushRuleComponent( ge );

        builder.build( context,
                       utils,
                       rce );
        
        context.pop();
        context.popRuleComponent();
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        final GroupElement ge = (GroupElement) rce;

        final ReteooComponentBuilder builder = this.geBuilders.get( ge.getType() );

        return builder.requiresLeftActivation( utils,
                                               rce );
    }
    
    public static class AndBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         * 
         * And group elements just iterate over their children
         * selecting and calling the build procedure for each one
         */
        public void build(final BuildContext context,
                          final BuildUtils utils,
                          final RuleConditionElement rce) {

            final GroupElement ge = (GroupElement) rce;

            // iterate over each child and build it
            for (final RuleConditionElement child : ge.getChildren()) {

                final ReteooComponentBuilder builder = utils.getBuilderFor(child);

                builder.build( context,
                               utils,
                               child );

                // if a previous object source was bound, but no tuple source
                if (context.getObjectSource() != null && context.getTupleSource() == null) {
                    // we know this is the root OTN, so record it
                    ObjectSource source = context.getObjectSource();
                    while ( !(source instanceof ObjectTypeNode) ) {
                        source = source.getParentObjectSource();
                    }
                    context.setRootObjectTypeNode( (ObjectTypeNode) source );
                    
                    
                    // adapt it to a Tuple source                    
                    context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                                                                                ReteooComponentFactory.getNodeFactoryService().buildLeftInputAdapterNode( context.getNextId(),
                                                                                                                                                          context.getObjectSource(),
                                                                                                                                                          context ) ) );

                    context.setObjectSource( null );
                }

                // if there was a previous tuple source, then a join node is needed
                if (context.getObjectSource() != null && context.getTupleSource() != null) {
                    // so, create the tuple source and clean up the constraints and object source
                    final BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context,
                                                                                            context.getBetaconstraints(),
                                                                                            false );

                    context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                                                                                ReteooComponentFactory.getNodeFactoryService().buildJoinNode( context.getNextId(),
                                                                                                                                              context.getTupleSource(),
                                                                                                                                              context.getObjectSource(),
                                                                                                                                              betaConstraints,
                                                                                                                                              context) ) );
                    context.setBetaconstraints( null );
                    context.setObjectSource( null );
                }
            }
        }

        public boolean requiresLeftActivation(final BuildUtils utils,
                                              final RuleConditionElement rce) {
            final GroupElement and = (GroupElement) rce;

            // need to check this because in the case of an empty rule, the root AND
            // will have no child
            if ( and.getChildren().isEmpty() ) {
                return true;
            }

            final RuleConditionElement child = and.getChildren().get( 0 );
            final ReteooComponentBuilder builder = utils.getBuilderFor( child );

            return builder.requiresLeftActivation( utils,
                                                   child );
        }
    }

    public static class OrBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         */
        public void build(final BuildContext context,
                          final BuildUtils utils,
                          final RuleConditionElement rce) {
            throw new RuntimeDroolsException( "BUG: Can't build a rete network with an inner OR group element" );
        }

        public boolean requiresLeftActivation(final BuildUtils utils,
                                              final RuleConditionElement rce) {
            throw new RuntimeDroolsException( "BUG: Can't build a rete network with an inner OR group element" );
        }
    }

    public static class NotBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         * 
         * Not must verify what is the class of its child:
         * 
         * If it is a pattern, a simple NotNode is added to the rulebase
         * If it is a group element, than a subnetwork must be created
         */
        public void build(final BuildContext context,
                          final BuildUtils utils,
                          final RuleConditionElement rce) {
            boolean existSubNetwort = false;
            final GroupElement not = (GroupElement) rce;

            // NOT must save some context info to restore it later
            final int currentPatternIndex = context.getCurrentPatternOffset();
            final LeftTupleSource tupleSource = context.getTupleSource();

            // get child
            final RuleConditionElement child = not.getChildren().get( 0 );

            // get builder for child
            final ReteooComponentBuilder builder = utils.getBuilderFor( child );

            // builds the child
            builder.build( context,
                           utils,
                           child );

            // if it is a subnetwork
            if ( context.getObjectSource() == null && context.getTupleSource() != null ) {

                // attach right input adapter node to convert tuple source into an object source
                context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                          new RightInputAdapterNode( context.getNextId(),
                                                                                                     context.getTupleSource(),
                                                                                                     context ) ) );

                // restore tuple source from before the start of the sub network
                context.setTupleSource( tupleSource );

                // create a tuple start equals constraint and set it in the context
                final TupleStartEqualsConstraint constraint = TupleStartEqualsConstraint.getInstance();
                final List<BetaNodeFieldConstraint> predicates = new ArrayList<BetaNodeFieldConstraint>();
                predicates.add( constraint );
                context.setBetaconstraints( predicates );
                existSubNetwort = true;

            }
            
            if ( !context.isTupleMemoryEnabled() && existSubNetwort ) {
                // If there is a RIANode, so need to handle. This only happens with queries, so need to worry about sharing
                context.setTupleSource( (LeftTupleSource) utils.attachNode( context, new QueryRiaFixerNode( context.getNextId(), context.getTupleSource(), context ) ) );   
            }              

            final BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context,
                                                                                    context.getBetaconstraints(),
                                                                                    false );
            // then attach the NOT node. It will work both as a simple not node
            // or as subnetwork join node as the context was set appropriatelly
            // in each case
            NotNode node = new NotNode( context.getNextId(),
                                        context.getTupleSource(),
                                        context.getObjectSource(),
                                        betaConstraints,
                                        context );
            context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                                                                        node ) );
            context.setBetaconstraints( null );
            context.setObjectSource( null );

            // restore pattern index
            context.setCurrentPatternOffset( currentPatternIndex );
        }

        public boolean requiresLeftActivation(final BuildUtils utils,
                                              final RuleConditionElement rce) {
            return true;
        }
    }

    public static class ExistsBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         * 
         * Exists must verify what is the class of its child:
         * 
         * If it is a pattern, a simple ExistsNode is added to the rulebase
         * If it is a group element, than a subnetwork must be created
         */
        public void build(final BuildContext context,
                          final BuildUtils utils,
                          final RuleConditionElement rce) {
            boolean existSubNetwort = false;            
            final GroupElement exists = (GroupElement) rce;

            // EXISTS must save some context info to restore it later
            final int currentPatternIndex = context.getCurrentPatternOffset();
            final LeftTupleSource tupleSource = context.getTupleSource();

            // get child
            final RuleConditionElement child = exists.getChildren().get( 0 );

            // get builder for child
            final ReteooComponentBuilder builder = utils.getBuilderFor( child );

            // builds the child
            builder.build( context,
                           utils,
                           child );

            // if it is a subnetwork
            if ( context.getObjectSource() == null && context.getTupleSource() != null ) {

                // attach right input adapter node to convert tuple source into an object source
                context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                          new RightInputAdapterNode( context.getNextId(),
                                                                                                     context.getTupleSource(),
                                                                                                     context ) ) );

                // restore tuple source from before the start of the sub network
                context.setTupleSource( tupleSource );

                // create a tuple start equals constraint and set it in the context
                final TupleStartEqualsConstraint constraint = TupleStartEqualsConstraint.getInstance();
                final List<BetaNodeFieldConstraint> predicates = new ArrayList<BetaNodeFieldConstraint>();
                predicates.add( constraint );
                context.setBetaconstraints( predicates );
                existSubNetwort = true;                

            }
            
            if ( !context.isTupleMemoryEnabled() && existSubNetwort ) {
                // If there is a RIANode, so need to handle. This only happens with queries, so need to worry about sharing
                context.setTupleSource( (LeftTupleSource) utils.attachNode( context, new QueryRiaFixerNode( context.getNextId(), context.getTupleSource(), context ) ) );   
            }             

            final BetaConstraints betaConstraints = utils.createBetaNodeConstraint( context,
                                                                                    context.getBetaconstraints(),
                                                                                    false );

            // then attach the EXISTS node. It will work both as a simple exists node
            // or as subnetwork join node as the context was set appropriatelly
            // in each case
            context.setTupleSource( (LeftTupleSource) utils.attachNode( context,
                                                                        new ExistsNode( context.getNextId(),
                                                                                        context.getTupleSource(),
                                                                                        context.getObjectSource(),
                                                                                        betaConstraints,
                                                                                        context ) ) );
            context.setBetaconstraints( null );
            context.setObjectSource( null );

            // restore pattern index
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

}
