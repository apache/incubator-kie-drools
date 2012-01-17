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
import java.util.List;

import org.drools.ActivationListenerFactory;
import org.drools.RuleIntegrationException;
import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.InternalRuleBase;
import org.drools.conf.EventProcessingOption;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.TerminalNode;
import org.drools.rule.Accumulate;
import org.drools.rule.Collect;
import org.drools.rule.EntryPoint;
import org.drools.rule.EvalCondition;
import org.drools.rule.Forall;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.Pattern;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.time.TemporalDependencyMatrix;

public class ReteooRuleBuilder {

    private BuildUtils utils;

    public ReteooRuleBuilder() {
        this.utils = new BuildUtils();

        this.utils.addBuilder( GroupElement.class,
                               new GroupElementBuilder() );
        this.utils.addBuilder( Pattern.class,
                               new PatternBuilder() );
        this.utils.addBuilder( EvalCondition.class,
                               new EvalBuilder() );
        this.utils.addBuilder( QueryElement.class,
                               new QueryElementBuilder() );
        this.utils.addBuilder( From.class,
                               new FromBuilder() );
        this.utils.addBuilder( Collect.class,
                               new CollectBuilder() );
        this.utils.addBuilder( Accumulate.class,
                               new AccumulateBuilder() );
        this.utils.addBuilder( Forall.class,
                               new ForallBuilder() );
        this.utils.addBuilder( EntryPoint.class,
                               new EntryPointBuilder() );
    }

    /**
     * Creates the corresponting Rete network for the given <code>Rule</code> and adds it to
     * the given rule base.
     * 
     * @param rule
     *            The rule to add.
     * @param rulebase
     *            The rulebase to add the rule to.
     *            
     * @return a List<BaseNode> of terminal nodes for the rule             
     * 
     * @throws RuleIntegrationException
     *             if an error prevents complete construction of the network for
     *             the <code>Rule</code>.
     * @throws InvalidPatternException
     */
    public List<TerminalNode> addRule( final Rule rule,
            final InternalRuleBase rulebase,
            final ReteooBuilder.IdGenerator idGenerator ) throws InvalidPatternException {
        // the list of terminal nodes
        final List<TerminalNode> nodes = new ArrayList<TerminalNode>();

        // transform rule and gets the array of subrules
        final GroupElement[] subrules = rule.getTransformedLhs();

        for (int i = 0; i < subrules.length; i++) {

            // creates a clean build context for each subrule
            final BuildContext context = new BuildContext( rulebase,
                                                           idGenerator );
            context.setRule( rule );

            // if running in STREAM mode, calculate temporal distance for events
            if (EventProcessingOption.STREAM.equals( rulebase.getConfiguration().getEventProcessingMode() )) {
                TemporalDependencyMatrix temporal = this.utils.calculateTemporalDistance( subrules[i] );
                context.setTemporalDistance( temporal );
            }

            if (rulebase.getConfiguration().isSequential()) {
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
                context.setAlphaNodeMemoryAllowed( false );
            } else {
                context.setTupleMemoryEnabled( true );
                context.setObjectTypeNodeMemoryEnabled( true );
                context.setAlphaNodeMemoryAllowed( true );
            }

            // adds subrule
            final TerminalNode node = this.addSubRule( context,
                                                       subrules[i],
                                                       i,
                                                       rule );

            // adds the terminal node to the list of terminal nodes
            nodes.add( node );

        }

        return nodes;
    }

    private TerminalNode addSubRule( final BuildContext context,
            final GroupElement subrule,
            final int subruleIndex,
            final Rule rule ) throws InvalidPatternException {
        // gets the appropriate builder
        final ReteooComponentBuilder builder = this.utils.getBuilderFor( subrule );

        // checks if an initial-fact is needed
        if (builder.requiresLeftActivation( this.utils,
                                            subrule )) {
            this.addInitialFactPattern( context,
                                        subrule,
                                        rule );
        }

        // builds and attach
        builder.build( context,
                       this.utils,
                       subrule );

        ActivationListenerFactory factory = context.getRuleBase().getConfiguration().getActivationListenerFactory( rule.getActivationListener() );
        TerminalNode terminal = factory.createActivationListener( context.getNextId(),
                                                                  context.getTupleSource(),
                                                                  rule,
                                                                  subrule,
                                                                  subruleIndex,
                                                                  context );

        if (context.getWorkingMemories().length == 0) {
            ( (BaseNode) terminal ).attach();
        } else {
            ( (BaseNode) terminal ).attach( context.getWorkingMemories() );
        }

        ( (BaseNode) terminal ).networkUpdated();

        // adds the terminal no to the list of nodes created/added by this sub-rule
        context.getNodes().add( (BaseNode) terminal );

        // assigns partition IDs to the new nodes
        //assignPartitionId(context);

        return terminal;
    }

    /**
     * Adds a query pattern to the given subrule
     * 
     * @param context
     * @param subrule
     * @param rule
     */
    private void addInitialFactPattern( final BuildContext context,
            final GroupElement subrule,
            final Rule rule ) {

        // creates a pattern for initial fact
        final Pattern pattern = new Pattern( 0,
                                             ClassObjectType.InitialFact_ObjectType );

        // adds the pattern as the first child of the given AND group element
        subrule.addChild( 0,
                          pattern );
    }

    public void addEntryPoint( final String id,
            final InternalRuleBase rulebase,
            final ReteooBuilder.IdGenerator idGenerator ) {
        // creates a clean build context for each subrule
        final BuildContext context = new BuildContext( rulebase,
                                                       idGenerator );
        EntryPoint ep = new EntryPoint( id );
        ReteooComponentBuilder builder = utils.getBuilderFor( ep );
        builder.build( context,
                       utils,
                       ep );

    }

}
