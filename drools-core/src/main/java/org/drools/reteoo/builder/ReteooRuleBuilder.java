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
import java.util.List;
import java.util.Map;

import org.drools.InitialFact;
import org.drools.RuleIntegrationException;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.common.BaseNode;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.rule.Accumulate;
import org.drools.rule.Collect;
import org.drools.rule.Column;
import org.drools.rule.EvalCondition;
import org.drools.rule.Forall;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.InvalidPatternException;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.FieldValue;

/**
 * @author etirelli
 *
 */
public class ReteooRuleBuilder {

    private BuildUtils utils;

    public ReteooRuleBuilder() {
        this.utils = new BuildUtils();

        utils.addBuilder( GroupElement.class,
                          new GroupElementBuilder() );
        utils.addBuilder( Column.class,
                          new ColumnBuilder() );
        utils.addBuilder( EvalCondition.class,
                          new EvalBuilder() );
        utils.addBuilder( From.class,
                          new FromBuilder() );
        utils.addBuilder( Collect.class,
                          new CollectBuilder() );
        utils.addBuilder( Accumulate.class,
                          new AccumulateBuilder() );
        utils.addBuilder( Forall.class,
                          new ForallBuilder() );
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
    public List addRule(final Rule rule,
                        final ReteooRuleBase rulebase,
                        final Map attachedNodes,
                        final ReteooBuilder.IdGenerator idGenerator) throws InvalidPatternException {

        // the list of terminal nodes
        final List nodes = new ArrayList();

        // transform rule and gets the array of subrules
        final GroupElement[] subrules = rule.getTransformedLhs();

        for ( int i = 0; i < subrules.length; i++ ) {
            // creates a clean build context for each subrule
            BuildContext context = new BuildContext( rulebase,
                                                     attachedNodes,
                                                     idGenerator );
            // adds subrule
            TerminalNode node = this.addSubRule( context,
                                                 subrules[i],
                                                 rule );

            // adds the terminal node to the list of terminal nodes
            nodes.add( node );

        }

        return nodes;
    }

    private TerminalNode addSubRule(BuildContext context,
                                    final GroupElement subrule,
                                    final Rule rule) throws InvalidPatternException {

        // if it is a query, needs to add the query column
        if ( rule instanceof Query ) {
            this.addQueryColumn( context,
                                 subrule,
                                 (Query) rule );
        }

        // gets the appropriate builder
        ReteooComponentBuilder builder = this.utils.getBuilderFor( subrule );

        // checks if an initial-fact is needed
        if ( builder.requiresLeftActivation( utils,
                                             subrule ) ) {
            this.addInitialFactColumn( context,
                                       subrule,
                                       rule );
        }

        // builds and attach
        builder.build( context,
                       this.utils,
                       subrule );

        TerminalNode terminal = null;

        if ( !(rule instanceof Query) ) {
            // Check a consequence is set
            if ( rule.getConsequence() == null ) {
                throw new InvalidPatternException( "Rule '" + rule.getName() + "' has no Consequence" );
            }
            terminal = new RuleTerminalNode( context.getNextId(),
                                             context.getTupleSource(),
                                             rule,
                                             subrule );
        } else {
            // Check there is no consequence
            if ( rule.getConsequence() != null ) {
                throw new InvalidPatternException( "Query '" + rule.getName() + "' should have no Consequence" );
            }
            terminal = new QueryTerminalNode( context.getNextId(),
                                              context.getTupleSource(),
                                              rule,
                                              subrule );
        }
        if ( context.getWorkingMemories().length == 0 ) {
            ((BaseNode) terminal).attach();
        } else {
            ((BaseNode) terminal).attach( context.getWorkingMemories() );
        }

        return terminal;
    }

    /**
     * Adds a query column to the given subrule
     * 
     * @param context
     * @param subrule
     * @param query
     */
    private void addQueryColumn(final BuildContext context,
                                final GroupElement subrule,
                                final Query query) {

        // creates a column for initial fact
        final Column column = new Column( 0,
                                          new ClassObjectType( DroolsQuery.class ) );

        final ClassFieldExtractor extractor = new ClassFieldExtractor( DroolsQuery.class,
                                                                       "name" );

        final FieldValue field = FieldFactory.getFieldValue( query.getName(),
                                                             ValueType.STRING_TYPE );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    ValueType.STRING_TYPE.getEvaluator( Operator.EQUAL ),
                                                                    field );

        // adds appropriate constraint to the column
        column.addConstraint( constraint );

        // adds the column as the first child of the given AND group element
        subrule.addChild( 0,
                          column );

    }

    /**
     * Adds a query column to the given subrule
     * 
     * @param context
     * @param subrule
     * @param query
     */
    private void addInitialFactColumn(final BuildContext context,
                                      final GroupElement subrule,
                                      final Rule rule) {

        // creates a column for initial fact
        final Column column = new Column( 0,
                                          new ClassObjectType( InitialFact.class ) );

        // adds the column as the first child of the given AND group element
        subrule.addChild( 0,
                          column );
    }

}
