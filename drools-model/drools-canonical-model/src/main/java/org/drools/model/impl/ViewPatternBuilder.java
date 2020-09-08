/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.ConditionalConsequence;
import org.drools.model.Consequence;
import org.drools.model.PatternDSL.ExchangeDefImpl;
import org.drools.model.PatternDSL.PatternBindingImpl;
import org.drools.model.PatternDSL.PatternDefImpl;
import org.drools.model.PatternDSL.PatternExprImpl;
import org.drools.model.PatternDSL.PatternItem;
import org.drools.model.RuleItem;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.patterns.AccumulatePatternImpl;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.EvalImpl;
import org.drools.model.patterns.ExistentialPatternImpl;
import org.drools.model.patterns.GroupByPatternImpl;
import org.drools.model.patterns.PatternImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.model.view.AccumulateExprViewItem;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExistentialExprViewItem;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.FixedValueItem;
import org.drools.model.view.GroupByExprViewItem;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.ViewItem;

import static java.util.stream.Collectors.toList;

import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.model.impl.ViewFlowBuilder.createConditionalNamedConsequence;
import static org.drools.model.impl.ViewFlowBuilder.createConstraint;

public class ViewPatternBuilder implements ViewBuilder {

    ViewPatternBuilder() { }

    public CompositePatterns apply( RuleItemBuilder<?>[] viewItemBuilders ) {
        List<RuleItem> ruleItems = Stream.of( viewItemBuilders ).map( RuleItemBuilder::get ).collect( toList() );
        Iterator<RuleItem> ruleItemIterator = ruleItems.iterator();

        List<Condition> conditions = new ArrayList<>();
        Map<String, Consequence> consequences = new LinkedHashMap<>();

        while (ruleItemIterator.hasNext()) {
            RuleItem ruleItem = ruleItemIterator.next();

            if (ruleItem instanceof Consequence) {
                Consequence consequence = (Consequence) ruleItem;
                String name = ruleItemIterator.hasNext() ? generateName("consequence") : RuleImpl.DEFAULT_CONSEQUENCE_NAME;
                consequences.put(name, consequence);
                conditions.add( new NamedConsequenceImpl( name, consequence.isBreaking() ) );
                continue;
            }

            if (ruleItem instanceof ConditionalConsequence ) {
                conditions.add( createConditionalNamedConsequence(consequences, (ConditionalConsequence) ruleItem) );
                continue;
            }

            conditions.add( ruleItem2Condition( ruleItem ) );
        }

        return new CompositePatterns( Condition.Type.AND, conditions, consequences );
    }

    public static Condition ruleItem2Condition(RuleItem ruleItem) {
        if ( ruleItem instanceof PatternDefImpl ) {
            PatternDefImpl<?> patternDef = ( PatternDefImpl ) ruleItem;
            Variable<?> patternVariable = patternDef.getFirstVariable();
            PatternImpl pattern = new PatternImpl( patternVariable, patternVariable instanceof Exchange ? Condition.Type.RECEIVER : Condition.Type.PATTERN );
            for (PatternItem patternItem : patternDef.getItems()) {
                if ( patternItem instanceof PatternExprImpl ) {
                    pattern.addConstraint( (( PatternExprImpl ) patternItem).asConstraint( patternDef ) );
                } else if ( patternItem instanceof PatternBindingImpl ) {
                    pattern.addBinding( (( PatternBindingImpl ) patternItem).asBinding( patternDef ) );
                } else {
                    throw new UnsupportedOperationException( "Unknown pattern item type: " + patternItem );
                }
            }
            pattern.addWatchedProps(patternDef.getWatch());
            return pattern;
        }

        if (ruleItem instanceof FixedValueItem ) {
            return new EvalImpl( (( FixedValueItem ) ruleItem).isValue() );
        }

        if ( ruleItem instanceof QueryCallViewItem ) {
            return new QueryCallPattern( (QueryCallViewItem) ruleItem );
        }

        if ( ruleItem instanceof CombinedExprViewItem ) {
            CombinedExprViewItem combined = ( CombinedExprViewItem ) ruleItem;
            List<Condition> conditions = new ArrayList<>();
            for (ViewItem expr : combined.getExpressions()) {
                conditions.add(ruleItem2Condition( expr ));
            }
            return new CompositePatterns( combined.getType(), conditions );
        }

        if ( ruleItem instanceof ExistentialExprViewItem ) {
            ExistentialExprViewItem existential = (ExistentialExprViewItem) ruleItem;
            return new ExistentialPatternImpl( ruleItem2Condition( existential.getExpression() ), existential.getType() );
        }

        if ( ruleItem instanceof GroupByExprViewItem ) {
            GroupByExprViewItem groupBy = ( GroupByExprViewItem ) ruleItem;
            return new GroupByPatternImpl(ruleItem2Condition( groupBy.getExpr() ),
                    groupBy.getVars(), groupBy.getVarKey(), groupBy.getGroupingFunction(),
                    groupBy.getAccumulateFunctions());
        }

        if ( ruleItem instanceof AccumulateExprViewItem ) {
            AccumulateExprViewItem acc = (AccumulateExprViewItem) ruleItem;
            return new AccumulatePatternImpl(ruleItem2Condition( acc.getExpr() ), acc.getAccumulateFunctions());
        }

        if ( ruleItem instanceof ExprViewItem ) {
            return new EvalImpl( createConstraint( (ExprViewItem) ruleItem ) );
        }

        if ( ruleItem instanceof ExchangeDefImpl ) {
            ExchangeDefImpl<?> exchangeDef = ( ExchangeDefImpl ) ruleItem;
            return new PatternImpl( exchangeDef.getFirstVariable(), Condition.Type.SENDER );
        }

        throw new UnsupportedOperationException( "Unknown " + ruleItem );
    }
}
