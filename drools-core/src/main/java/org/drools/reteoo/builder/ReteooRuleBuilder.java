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
import org.drools.common.UpdateContext;
import org.drools.conf.EventProcessingOption;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.RuleBuilder;
import org.drools.reteoo.TerminalNode;
import org.drools.reteoo.WindowNode;
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
import org.drools.rule.WindowDeclaration;
import org.drools.rule.WindowReference;
import org.drools.time.TemporalDependencyMatrix;

import static org.drools.reteoo.PropertySpecificUtil.isPropertyReactive;

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
        this.utils.addBuilder( Forall.class,
                               new ForallBuilder() );
        this.utils.addBuilder( EntryPoint.class,
                               new EntryPointBuilder() );
        this.utils.addBuilder( WindowReference.class, 
                               new WindowReferenceBuilder() );
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
        final GroupElement[] subrules = rule.getTransformedLhs( rulebase.getConfiguration().getComponentFactory().getLogicTransformer() );

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
            this.addInitialFactPattern( subrule );
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

        BaseNode baseTerminalNode = (BaseNode) terminal;
        baseTerminalNode.attach(context);

        baseTerminalNode.networkUpdated(new UpdateContext());

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
            final InternalRuleBase rulebase,
            final ReteooBuilder.IdGenerator idGenerator ) {
        // creates a clean build context for each subrule
        final BuildContext context = new BuildContext( rulebase,
                                                       idGenerator );
        EntryPoint ep = new EntryPoint( id );
        ReteooComponentBuilder builder = utils.getBuilderFor( ep );
        builder.build(context,
                utils,
                ep);
    }

    public WindowNode addWindowNode( WindowDeclaration window, 
                                     InternalRuleBase ruleBase, 
                                     ReteooBuilder.IdGenerator idGenerator ) {
        // creates a clean build context for each subrule
        final BuildContext context = new BuildContext( ruleBase,
                                                       idGenerator );
        
        if ( ruleBase.getConfiguration().isSequential() ) {
            context.setTupleMemoryEnabled( false );
            context.setObjectTypeNodeMemoryEnabled( false );
            context.setAlphaNodeMemoryAllowed( false );
        } else {
            context.setTupleMemoryEnabled( true );
            context.setObjectTypeNodeMemoryEnabled( true );
            context.setAlphaNodeMemoryAllowed( true );
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
