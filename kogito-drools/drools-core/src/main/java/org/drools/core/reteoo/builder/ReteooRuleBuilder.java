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

package org.drools.core.reteoo.builder;

import org.drools.core.ActivationListenerFactory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.UpdateContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.AddRemoveRule;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RuleBuilder;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Collect;
import org.drools.core.rule.ConditionalBranch;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Forall;
import org.drools.core.rule.From;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.InvalidPatternException;
import org.drools.core.rule.NamedConsequence;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.QueryElement;
import org.drools.core.rule.WindowDeclaration;
import org.drools.core.rule.WindowReference;
import org.drools.core.time.TemporalDependencyMatrix;
import org.drools.core.time.impl.Timer;
import org.kie.api.conf.EventProcessingOption;

import java.util.ArrayList;
import java.util.List;

public class ReteooRuleBuilder implements RuleBuilder {

    protected BuildUtils utils;

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
        this.utils.addBuilder( Timer.class,
                               new TimerBuilder() );
        this.utils.addBuilder( Forall.class,
                               new ForallBuilder() );
        this.utils.addBuilder( EntryPointId.class,
                               new EntryPointBuilder() );
        this.utils.addBuilder( WindowReference.class, 
                               new WindowReferenceBuilder() );
        this.utils.addBuilder( NamedConsequence.class,
                               new NamedConsequenceBuilder() );
        this.utils.addBuilder( ConditionalBranch.class,
                               new ConditionalBranchBuilder() );
    }

    /**
     * Creates the corresponting Rete network for the given <code>Rule</code> and adds it to
     * the given rule base.
     * 
     * @param rule
     *            The rule to add.
     * @param kBase
     *            The rulebase to add the rule to.
     *            
     * @return a List<BaseNode> of terminal nodes for the rule             
     * @throws InvalidPatternException
     */
    public List<TerminalNode> addRule( final RuleImpl rule,
            final InternalKnowledgeBase kBase,
            final ReteooBuilder.IdGenerator idGenerator ) throws InvalidPatternException {
        // the list of terminal nodes
        final List<TerminalNode> nodes = new ArrayList<TerminalNode>();

        // transform rule and gets the array of subrules
        final GroupElement[] subrules = rule.getTransformedLhs( kBase.getConfiguration().getComponentFactory().getLogicTransformerFactory().getLogicTransformer(),
                                                                kBase.getGlobals() );

        for (int i = 0; i < subrules.length; i++) {

            // creates a clean build context for each subrule
            final BuildContext context = new BuildContext( kBase,
                                                           idGenerator );
            context.setRule( rule );

            // if running in STREAM mode, calculate temporal distance for events
            if (EventProcessingOption.STREAM.equals( kBase.getConfiguration().getEventProcessingMode() )) {
                TemporalDependencyMatrix temporal = this.utils.calculateTemporalDistance( subrules[i] );
                context.setTemporalDistance( temporal );
            }

            if (kBase.getConfiguration().isSequential() ) {
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
            } else {
                context.setTupleMemoryEnabled( true );
                context.setObjectTypeNodeMemoryEnabled( true );
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
                                     final RuleImpl rule ) throws InvalidPatternException {
        context.setSubRule(subrule);

        // gets the appropriate builder
        ReteooComponentBuilder builder = this.utils.getBuilderFor( subrule );

        // checks if an initial-fact is needed
        if (builder.requiresLeftActivation( this.utils,
                                            subrule )) {
            this.addInitialFactPattern( subrule );
        }

        // builds and attach
        builder.build( context,
                       this.utils,
                       subrule );

        if  ( context.getKnowledgeBase().getConfiguration().isPhreakEnabled() && rule.getTimer() != null ) {
            builder = this.utils.getBuilderFor( Timer.class );
            builder.build( context, this.utils, rule.getTimer() );
        }

        ActivationListenerFactory factory = context.getKnowledgeBase().getConfiguration().getActivationListenerFactory( rule.getActivationListener() );
        TerminalNode terminal = factory.createActivationListener( context.getNextId(),
                                                                  context.getTupleSource(),
                                                                  rule,
                                                                  subrule,
                                                                  subruleIndex,
                                                                  context );

        BaseNode baseTerminalNode = (BaseNode) terminal;
        baseTerminalNode.networkUpdated(new UpdateContext());
        baseTerminalNode.attach(context);
        if ( context.getKnowledgeBase().getConfiguration().isPhreakEnabled() ) {
            AddRemoveRule.addRule( terminal, context.getWorkingMemories(), context.getKnowledgeBase() );
        }

        // adds the terminal node to the list of nodes created/added by this sub-rule
        context.getNodes().add( baseTerminalNode );

        // assigns partition IDs to the new nodes
        //assignPartitionId(context);

        return terminal;
    }

    /**
     * Adds a query pattern to the given subrule
     * 
     * @param subrule
     */
    private void addInitialFactPattern( final GroupElement subrule ) {

        // creates a pattern for initial fact
        final Pattern pattern = new Pattern( 0,
                                             ClassObjectType.InitialFact_ObjectType );

        // adds the pattern as the first child of the given AND group element
        subrule.addChild( 0,
                          pattern );
    }

    public void addEntryPoint( final String id,
            final InternalKnowledgeBase kBase,
            final ReteooBuilder.IdGenerator idGenerator ) {
        // creates a clean build context for each subrule
        final BuildContext context = new BuildContext( kBase,
                                                       idGenerator );
        EntryPointId ep = new EntryPointId( id );
        ReteooComponentBuilder builder = utils.getBuilderFor( ep );
        builder.build(context,
                utils,
                ep);
    }

    public WindowNode addWindowNode( WindowDeclaration window,
                                     InternalKnowledgeBase kBase,
                                     ReteooBuilder.IdGenerator idGenerator ) {
        // creates a clean build context for each subrule
        final BuildContext context = new BuildContext( kBase,
                                                       idGenerator );
        
        if ( kBase.getConfiguration().isSequential() ) {
            context.setTupleMemoryEnabled( false );
            context.setObjectTypeNodeMemoryEnabled( false );
        } else {
            context.setTupleMemoryEnabled( true );
            context.setObjectTypeNodeMemoryEnabled( true );
        }
        
        // gets the appropriate builder
        final WindowBuilder builder = WindowBuilder.INSTANCE;

        // builds and attach
        builder.build( context,
                       this.utils,
                       window );

        return (WindowNode) context.getObjectSource();
    }

}
