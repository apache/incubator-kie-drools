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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.TupleSource;
import org.drools.rule.Column;
import org.drools.rule.GroupElement;
import org.drools.rule.RuleConditionElement;

/**
 * @author etirelli
 *
 */
public class GroupElementBuilder
    implements
    ReteooComponentBuilder {

    private Map geBuilders = new HashMap();

    public GroupElementBuilder() {
        geBuilders.put( GroupElement.AND,
                        new AndBuilder() );
        geBuilders.put( GroupElement.OR,
                        new OrBuilder() );
        geBuilders.put( GroupElement.NOT,
                        new NotBuilder() );
        geBuilders.put( GroupElement.EXISTS,
                        new ExistsBuilder() );
    }

    /**
     * @inheritDoc
     * 
     * 
     */
    public void build(BuildContext context,
                      BuildUtils utils,
                      RuleConditionElement rce) {
        GroupElement ge = (GroupElement) rce;

        ReteooComponentBuilder builder = (ReteooComponentBuilder) geBuilders.get( ge.getType() );

        builder.build( context,
                       utils,
                       rce );

        //        for ( final Iterator it = subrule.getChildren().iterator(); it.hasNext(); ) {
        //            final Object object = it.next();
        //
        //            if ( object instanceof EvalCondition ) {
        //                final EvalCondition eval = (EvalCondition) object;
        //                checkUnboundDeclarations( eval.getRequiredDeclarations() );
        //                this.tupleSource = attachNode( new EvalConditionNode( this.id++,
        //                                                                      this.tupleSource,
        //                                                                      eval ) );
        //                continue;
        //            }
        //
        //            BetaConstraints binder = null;
        //            Column column = null;
        //
        //            if ( object instanceof Column ) {
        //                column = (Column) object;
        //
        //                // @REMOVEME after the milestone period
        //                if ( (binder != null) && (binder != EmptyBetaConstraints.getInstance()) ) throw new RuntimeDroolsException( "This is a bug! Please report to Drools development team!" );
        //
        //                binder = attachColumn( (Column) object,
        //                                       subrule,
        //                                       this.removeIdentities );
        //
        //                // If a tupleSource does not exist then we need to adapt this
        //                // into
        //                // a TupleSource using LeftInputAdapterNode
        //                if ( this.tupleSource == null ) {
        //                    this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
        //                                                                             this.objectSource ) );
        //
        //                    // objectSource is created by the attachColumn method, if we
        //                    // adapt this to
        //                    // a TupleSource then we need to null the objectSource
        //                    // reference.
        //                    this.objectSource = null;
        //                }
        //            } else if ( object instanceof GroupElement ) {
        //                // If its not a Column or EvalCondition then it can either be a Not or an Exists
        //                GroupElement ce = (GroupElement) object;
        //                while ( !(ce.getChildren().get( 0 ) instanceof Column) ) {
        //                    ce = (GroupElement) ce.getChildren().get( 0 );
        //                }
        //                column = (Column) ce.getChildren().get( 0 );
        //
        //                // If a tupleSource does not exist then we need to adapt an
        //                // InitialFact into a a TupleSource using LeftInputAdapterNode
        //                if ( this.tupleSource == null ) {
        //                    // adjusting offset as all tuples will now contain initial-fact at index 0
        //                    this.currentOffsetAdjustment = 1;
        //
        //                    final ObjectSource objectSource = attachNode( new ObjectTypeNode( this.id++,
        //                                                                                      new ClassObjectType( InitialFact.class ),
        //                                                                                      this.rete,
        //                                                                                      this.ruleBase.getConfiguration().getAlphaNodeHashingThreshold() ) );
        //
        //                    this.tupleSource = attachNode( new LeftInputAdapterNode( this.id++,
        //                                                                             objectSource ) );
        //                }
        //
        //                // @REMOVEME after the milestone period
        //                if ( (binder != null) && (binder != EmptyBetaConstraints.getInstance()) ) throw new RuntimeDroolsException( "This is a bug! Please report to Drools development team!" );
        //
        //                binder = attachColumn( column,
        //                                       subrule,
        //                                       this.removeIdentities );
        //            }
        //
        //            if ( (object instanceof GroupElement) && (((GroupElement) object).isNot()) ) {
        //                attachNot( this.tupleSource,
        //                           (GroupElement) object,
        //                           this.objectSource,
        //                           binder,
        //                           column );
        //                binder = null;
        //            } else if ( (object instanceof GroupElement) && (((GroupElement) object).isExists()) ) {
        //                attachExists( this.tupleSource,
        //                              (GroupElement) object,
        //                              this.objectSource,
        //                              binder,
        //                              column );
        //                binder = null;
        //            } else if ( object.getClass() == From.class ) {
        //                attachFrom( this.tupleSource,
        //                            (From) object );
        //            } else if ( object.getClass() == Accumulate.class ) {
        //                attachAccumulate( this.tupleSource,
        //                                  subrule,
        //                                  (Accumulate) object );
        //            } else if ( object.getClass() == Collect.class ) {
        //                attachCollect( this.tupleSource,
        //                               subrule,
        //                               (Collect) object );
        //            } else if ( this.objectSource != null ) {
        //                this.tupleSource = attachNode( new JoinNode( this.id++,
        //                                                             this.tupleSource,
        //                                                             this.objectSource,
        //                                                             binder ) );
        //                binder = null;
        //            }
        //        }

    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation( BuildUtils utils, RuleConditionElement rce ) {
        GroupElement ge = (GroupElement) rce;

        ReteooComponentBuilder builder = (ReteooComponentBuilder) geBuilders.get( ge.getType() );
        
        return builder.requiresLeftActivation( utils, rce );
    }

    private static class AndBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         * 
         * And group elements just iterate over their children
         * selecting and calling the build procedure for each one
         * 
         */
        public void build(BuildContext context,
                          BuildUtils utils,
                          RuleConditionElement rce) {

            GroupElement ge = (GroupElement) rce;

            // iterate over each child and build it
            for ( Iterator it = ge.getChildren().iterator(); it.hasNext(); ) {

                RuleConditionElement child = (RuleConditionElement) it.next();

                ReteooComponentBuilder builder = utils.getBuilderFor( child );

                builder.build( context,
                               utils,
                               child );

                // if a previous object source was bound, but no tuple source
                if ( context.getObjectSource() != null && context.getTupleSource() == null ) {
                    // adapt it to a Tuple source
                    context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                            new LeftInputAdapterNode( context.getNextId(),
                                                                                                      context.getObjectSource() ) ) );

                    context.setObjectSource( null );
                }

                // if there was a previous tuple source, then a join node is needed
                if ( context.getObjectSource() != null && context.getTupleSource() != null ) {
                    // so, create the tuple source and clean up the constraints and object source
                    context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                            new JoinNode( context.getNextId(),
                                                                                          context.getTupleSource(),
                                                                                          context.getObjectSource(),
                                                                                          context.getBetaconstraints() ) ) );
                    context.setBetaconstraints( null );
                    context.setObjectSource( null );
                }
            }
        }

        public boolean requiresLeftActivation(BuildUtils utils, RuleConditionElement rce) {
            GroupElement and = (GroupElement) rce;

            // need to check this because in the case of an empty rule, the root AND
            // will have no child
            if( and.getChildren().isEmpty() ) {
                return true;
            } 
            
            RuleConditionElement child = (RuleConditionElement) and.getChildren().get( 0 );
            ReteooComponentBuilder builder = utils.getBuilderFor( child );
            
            return builder.requiresLeftActivation( utils, child );
        }
    }

    private static class OrBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         */
        public void build(BuildContext context,
                          BuildUtils utils,
                          RuleConditionElement rce) {
            throw new RuntimeDroolsException( "BUG: Can't build a rete network with an inner OR group element" );
        }

        public boolean requiresLeftActivation(BuildUtils utils, RuleConditionElement rce) {
            return false;
        }
    }

    private static class NotBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         * 
         * Not must verify what is the class of its child:
         * 
         * If it is a column, a simple NotNode is added to the rulebase
         * If it is a group element, than a subnetwork must be created
         */
        public void build(BuildContext context,
                          BuildUtils utils,
                          RuleConditionElement rce) {
            GroupElement not = (GroupElement) rce;
            
            // NOT must save current column index in order to restore it later
            int currentColumnIndex = context.getCurrentColumnOffset();

            // get child
            RuleConditionElement child = (RuleConditionElement) not.getChildren().get( 0 );

            // get builder for child
            ReteooComponentBuilder builder = utils.getBuilderFor( child );

            // builds the child
            builder.build( context,
                           utils,
                           child );

            // if child is a column 
            if ( child instanceof Column ) {
                // then no sub-network needed... just a simple NOT node
                context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                        new NotNode( context.getNextId(),
                                                                                     context.getTupleSource(),
                                                                                     context.getObjectSource(),
                                                                                     context.getBetaconstraints() ) ) );
                context.setBetaconstraints( null );
                context.setObjectSource( null );

            } else {
                // TODO: otherwise attach subnetwork
            }
            
            // restore column index
            context.setCurrentColumnOffset( currentColumnIndex );
        }

        public boolean requiresLeftActivation(BuildUtils utils,
                                              RuleConditionElement rce) {
            return true;
        }
    }

    private static class ExistsBuilder
        implements
        ReteooComponentBuilder {

        /**
         * @inheritDoc
         * 
         * Exists must verify what is the class of its child:
         * 
         * If it is a column, a simple ExistsNode is added to the rulebase
         * If it is a group element, than a subnetwork must be created
         */
        public void build(BuildContext context,
                          BuildUtils utils,
                          RuleConditionElement rce) {
            GroupElement exists = (GroupElement) rce;

            // EXISTS must save current column index in order to restore it later
            int currentColumnIndex = context.getCurrentColumnOffset();

            // get child
            RuleConditionElement child = (RuleConditionElement) exists.getChildren().get( 0 );

            // get builder for child
            ReteooComponentBuilder builder = utils.getBuilderFor( child );

            // builds the child
            builder.build( context,
                           utils,
                           child );

            // if child is a column 
            if ( child instanceof Column ) {
                // then no sub-network needed... just a simple EXISTS node
                context.setTupleSource( (TupleSource) utils.attachNode( context,
                                                                        new ExistsNode( context.getNextId(),
                                                                                     context.getTupleSource(),
                                                                                     context.getObjectSource(),
                                                                                     context.getBetaconstraints() ) ) );
                context.setBetaconstraints( null );
                context.setObjectSource( null );

            } else {
                // TODO: otherwise attach subnetwork
            }
            
            // restore column index
            context.setCurrentColumnOffset( currentColumnIndex );
        }

        /**
         * @inheritDoc
         */
        public boolean requiresLeftActivation(BuildUtils utils,
                                              RuleConditionElement rce) {
            return true;
        }
    }

}
