/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.drools.model.Argument;
import org.drools.model.Binding;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.ConditionalConsequence;
import org.drools.model.Consequence;
import org.drools.model.Declaration;
import org.drools.model.DeclarationSource;
import org.drools.model.From;
import org.drools.model.Global;
import org.drools.model.Pattern;
import org.drools.model.RuleItem;
import org.drools.model.RuleItemBuilder;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.consequences.ConditionalNamedConsequenceImpl;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.constraints.OrConstraints;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint10;
import org.drools.model.constraints.SingleConstraint11;
import org.drools.model.constraints.SingleConstraint12;
import org.drools.model.constraints.SingleConstraint13;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.SingleConstraint3;
import org.drools.model.constraints.SingleConstraint4;
import org.drools.model.constraints.SingleConstraint5;
import org.drools.model.constraints.SingleConstraint6;
import org.drools.model.constraints.SingleConstraint7;
import org.drools.model.constraints.SingleConstraint8;
import org.drools.model.constraints.SingleConstraint9;
import org.drools.model.constraints.TemporalConstraint;
import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.patterns.AccumulatePatternImpl;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.EvalImpl;
import org.drools.model.patterns.ExistentialPatternImpl;
import org.drools.model.patterns.PatternImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.model.view.AbstractExprViewItem;
import org.drools.model.view.AccumulateExprViewItem;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExistentialExprViewItem;
import org.drools.model.view.Expr10ViewItemImpl;
import org.drools.model.view.Expr11ViewItemImpl;
import org.drools.model.view.Expr12ViewItemImpl;
import org.drools.model.view.Expr13ViewItemImpl;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.Expr4ViewItemImpl;
import org.drools.model.view.Expr5ViewItemImpl;
import org.drools.model.view.Expr6ViewItemImpl;
import org.drools.model.view.Expr7ViewItemImpl;
import org.drools.model.view.Expr8ViewItemImpl;
import org.drools.model.view.Expr9ViewItemImpl;
import org.drools.model.view.ExprNViewItem;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.FixedValueItem;
import org.drools.model.view.InputViewItem;
import org.drools.model.view.InputViewItemImpl;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.ViewItem;

import static java.util.stream.Collectors.toList;

import static org.drools.model.FlowDSL.input;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.model.impl.VariableImpl.GENERATED_VARIABLE_PREFIX;

public class ViewFlowBuilder implements ViewBuilder {

    ViewFlowBuilder() { }

    public CompositePatterns apply( RuleItemBuilder<?>[] viewItemBuilders ) {
        BuildContext ctx = new BuildContext( viewItemBuilders );
        return ensureVariablesDeclarationInView(viewItems2Condition( ctx, Type.AND, true ), ctx);
    }

    private static CompositePatterns ensureVariablesDeclarationInView(CompositePatterns view, BuildContext ctx) {
        ctx.inputs.keySet().removeAll(ctx.usedVars);
        int i = 0;
        for (Map.Entry<Variable<?>, InputViewItemImpl<?>> entry : ctx.inputs.entrySet()) {
            view.addCondition(i++, new PatternImpl(entry.getKey()));
            ctx.usedVars.add(entry.getKey());
        }

        view.ensureVariablesDeclarationInView();
        view.getSubConditions().sort( ConditionComparator.INSTANCE );
        return view;
    }

    private static CompositePatterns viewItems2Condition(BuildContext ctx, Condition.Type type, boolean topLevel) {
        List<Condition> conditions = new ArrayList<>();
        Map<Variable<?>, Condition> conditionMap = new HashMap<>();
        Map<String, Consequence> consequences = topLevel ? new LinkedHashMap<>() : null;
        Iterator<RuleItem> ruleItemIterator = ctx.ruleItems.iterator();

        while (ruleItemIterator.hasNext()) {
            Map<Variable<?>, InputViewItemImpl<?>> scopedInputs = type.createsScope() ? new LinkedHashMap<>( ctx.inputs ) : ctx.inputs;
            RuleItem ruleItem = ruleItemIterator.next();

            if ( topLevel && ruleItem instanceof AbstractExprViewItem && ( (AbstractExprViewItem) ruleItem ).isQueryExpression() ) {
                ctx.isQuery = true;
            }

            if (ruleItem instanceof FixedValueItem) {
                conditions.add( new EvalImpl( (( FixedValueItem ) ruleItem).isValue() ) );
                continue;
            }

            if (ruleItem instanceof Consequence) {
                if (!topLevel) {
                    throw new IllegalStateException("A consequence can be only a top level item");
                }
                Consequence consequence = (Consequence) ruleItem;
                String name = ruleItemIterator.hasNext() ? generateName("consequence") : RuleImpl.DEFAULT_CONSEQUENCE_NAME;
                consequences.put(name, consequence);
                conditions.add( new NamedConsequenceImpl( name, consequence.isBreaking() ) );
                continue;
            }

            if (ruleItem instanceof ConditionalConsequence) {
                if (!topLevel) {
                    throw new IllegalStateException("A consequence can be only a top level item");
                }
                conditions.add( createConditionalNamedConsequence(consequences, (ConditionalConsequence) ruleItem) );
                continue;
            }

            ViewItem viewItem = (ViewItem) ruleItem;
            if ( viewItem instanceof CombinedExprViewItem ) {
                CombinedExprViewItem combined = (CombinedExprViewItem) viewItem;
                if (combined.getType() == Type.OR) {
                    Condition condition = conditionMap.get(combined.getFirstVariable());
                    if (condition instanceof PatternImpl) {
                        PatternImpl pattern = ( PatternImpl ) condition;
                        PatternImpl orPattern = new PatternImpl( pattern.getPatternVariable() );
                        for (ViewItem expr : combined.getExpressions()) {
                            orPattern = ( PatternImpl ) viewItem2Condition( expr, orPattern, ctx );
                        }
                        pattern.addConstraint( new OrConstraints(orPattern.getConstraint().getChildren()) );
                        continue;
                    }
                }
                conditions.add( viewItems2Condition( new BuildContext( ctx, combined.getExpressions(), scopedInputs ), combined.getType(), false ) );
                continue;
            }

            if ( viewItem instanceof QueryCallViewItem ) {
                QueryCallViewItem query = ( (QueryCallViewItem) viewItem );
                for ( Argument arg : query.getArguments()) {
                    if (arg instanceof Variable) {
                        ctx.usedVars.add( ( (Variable) arg ));
                        ctx.boundVars.add( ( (Variable) arg ));
                    }
                }
                conditions.add( new QueryCallPattern( query ) );
                continue;
            }

            if ( viewItem instanceof Binding) {
                Binding bindViewItem = (Binding) viewItem;
                PatternImpl pattern = (PatternImpl) conditionMap.get(bindViewItem.getInputVariable());
                if (pattern == null) {
                    // This should probably be the bindViewItem.getBoundVariable() instead of the input
                    // as the input variables can be many
                    pattern = new PatternImpl( bindViewItem.getInputVariable() );
                    pattern.addWatchedProps( bindViewItem.getWatchedProps() );
                    ctx.usedVars.add( bindViewItem.getInputVariable() );
                    conditions.add( pattern );
                    conditionMap.put( bindViewItem.getInputVariable(), pattern );
                }
                pattern.addBinding( bindViewItem );
                ctx.usedVars.add(viewItem.getFirstVariable());
                ctx.addBinding(bindViewItem);
                scopedInputs.putIfAbsent( viewItem.getFirstVariable(), (InputViewItemImpl) input( viewItem.getFirstVariable() ) );
                continue;
            }

            Variable<?> patternVariable = findPatternVariable(viewItem, scopedInputs.keySet() );

            if ( viewItem instanceof InputViewItemImpl ) {
                scopedInputs.put( patternVariable, (InputViewItemImpl) viewItem );
                PatternImpl condition = new PatternImpl( patternVariable );
                condition.addWatchedProps( (( InputViewItemImpl ) viewItem).getWatchedProps() );
                conditions.add( condition );
                conditionMap.put( patternVariable, condition );
                ctx.usedVars.add( patternVariable );
                continue;
            }

            if ( viewItem instanceof ExistentialExprViewItem ) {
                ExistentialExprViewItem existential = ( (ExistentialExprViewItem) viewItem );
                if (patternVariable != null && !ctx.isQuery) {
                    ctx.addExistentialVar( patternVariable );
                    registerInputsFromViewItem( existential.getExpression(), conditionMap, scopedInputs, ctx );
                }
                Condition condition = new PatternImpl( patternVariable, SingleConstraint.TRUE, ctx.bindings.get(patternVariable) );
                Condition.Type existentialType = existential.getType();
                ViewItem existentialExpr = existential.getExpression();
                while (existentialExpr instanceof ExistentialExprViewItem) {
                    existentialExpr = (( ExistentialExprViewItem ) existentialExpr).getExpression();
                }
                conditions.add( new ExistentialPatternImpl( viewItem2Condition( existentialExpr, condition, ctx ), existentialType ) );
                continue;
            }

            boolean patternVariableIsGlobal = patternVariable instanceof Global;
            boolean isPatternVariableBound = ctx.boundVars.contains(patternVariable);
            if ( ruleItem instanceof ExprViewItem && (isPatternVariableBound || patternVariableIsGlobal)) {
                conditions.add( new EvalImpl( createConstraint( (ExprViewItem) ruleItem ) ) );
                continue;
            }

            ctx.usedVars.add( patternVariable );
            Condition condition;
            if ( type == Type.AND ) {
                condition = conditionMap.get( patternVariable );
                if ( condition == null ) {
                    condition = new PatternImpl( patternVariable, SingleConstraint.TRUE, ctx.bindings.get(patternVariable) );
                    conditions.add( condition );
                    if (!(viewItem instanceof AccumulateExprViewItem)) {
                        conditionMap.put( patternVariable, condition );
                    }
                    scopedInputs.putIfAbsent( patternVariable, (InputViewItemImpl) input( patternVariable ) );
                }
            } else {
                condition = new PatternImpl( patternVariable );
                conditions.add( condition );
            }

            addInputFromVariableSource( scopedInputs, patternVariable );

            Condition modifiedPattern = viewItem2Condition( viewItem, condition, new BuildContext( ctx, scopedInputs ) );
            conditions.set( conditions.indexOf( condition ), modifiedPattern );

            if (!ctx.isQuery) {
                registerInputsFromViewItem( viewItem, conditionMap, scopedInputs, ctx );
            }


            if ( type == Type.AND && !(viewItem instanceof AccumulateExprViewItem) ) {
                conditionMap.put( patternVariable, modifiedPattern );
            }
        }

        return new CompositePatterns( type, conditions, ctx.usedVars, consequences );
    }

    private static void registerInputsFromViewItem( ViewItem viewItem, Map<Variable<?>, Condition> conditionMap, Map<Variable<?>, InputViewItemImpl<?>> scopedInputs, BuildContext ctx ) {
        for (Variable var : viewItem.getVariables()) {
            if (var.isFact() && !conditionMap.containsKey( var ) && !ctx.isExistentialVar( var )) {
                scopedInputs.putIfAbsent( var, (InputViewItemImpl) input( var ) );
            }
        }
    }

    private static Variable<?> findPatternVariable(ViewItem viewItem, Set<Variable<?>> vars ) {
        Variable<?> patternVariable = findPatternSingleNonGeneratedVariable( viewItem );
        if (!vars.contains( patternVariable )) {
            return patternVariable;
        }

        // vars is an ordered set of the variables used so far
        // the pattern variable is the last used variables also present in the viewitem

        Variable<?>[] itemVars = viewItem.getVariables();
        for (Variable<?> var : vars) {
            for (Variable<?> itemVar : itemVars) {
                if (itemVar == var) {
                    patternVariable = itemVar;
                    break;
                }
            }
        }

        return patternVariable;
    }

    private static Variable<?> findPatternSingleNonGeneratedVariable( ViewItem viewItem ) {
        Variable<?> patternVariable = viewItem.getFirstVariable();
        if (viewItem instanceof AccumulateExprViewItem && patternVariable == null) {
            for (Variable itemVar : viewItem.getVariables()) {
                if (!itemVar.getName().contains( GENERATED_VARIABLE_PREFIX )) {
                    if (patternVariable == null) {
                        patternVariable = itemVar;
                    } else {
                        return null;
                    }
                }
            }
        }
        return patternVariable;
    }

    private static void addInputFromVariableSource( Map<Variable<?>, InputViewItemImpl<?>> inputs, Variable<?> patterVariable ) {
        if ( patterVariable instanceof Declaration ) {
            Declaration declaration = (( Declaration ) patterVariable);
            if ( declaration.getSource() instanceof From ) {
                Variable var = (( From ) declaration.getSource()).getVariable();
                if(var != null) { // If from is from a supplier source is missing
                    addInputFromVariableSource(inputs, var);
                    if (var.isFact()) {
                        inputs.putIfAbsent(var, (InputViewItemImpl) input(var));
                    }
                }
            }
        }
    }

    static ConditionalNamedConsequenceImpl createConditionalNamedConsequence(Map<String, Consequence> consequences, ConditionalConsequence cond) {
        return new ConditionalNamedConsequenceImpl( createConstraint( cond.getExpr() ),
                                                    createNamedConsequence( consequences, cond.getThen() ),
                                                    cond.getElse() != null ? createConditionalNamedConsequence( consequences, cond.getElse() ) : null );
    }

    static SingleConstraint createConstraint( ExprViewItem expr ) {
        if (expr instanceof Expr1ViewItemImpl) {
            return new SingleConstraint1( (Expr1ViewItemImpl) expr );
        }
        if (expr instanceof Expr2ViewItemImpl) {
            return new SingleConstraint2( (Expr2ViewItemImpl) expr );
        }
        if (expr instanceof Expr3ViewItemImpl) {
            return new SingleConstraint3( (Expr3ViewItemImpl) expr );
        }
        if (expr instanceof Expr4ViewItemImpl ) {
            return new SingleConstraint4( (Expr4ViewItemImpl) expr );
        }
        if (expr instanceof Expr5ViewItemImpl ) {
            return new SingleConstraint5( (Expr5ViewItemImpl) expr );
        }
        if (expr instanceof Expr6ViewItemImpl ) {
            return new SingleConstraint6( (Expr6ViewItemImpl) expr );
        }
        if (expr instanceof Expr7ViewItemImpl ) {
            return new SingleConstraint7( (Expr7ViewItemImpl) expr );
        }
        if (expr instanceof Expr8ViewItemImpl ) {
            return new SingleConstraint8( (Expr8ViewItemImpl) expr );
        }
        if (expr instanceof Expr9ViewItemImpl ) {
            return new SingleConstraint9( (Expr9ViewItemImpl) expr );
        }
        if (expr instanceof Expr10ViewItemImpl ) {
            return new SingleConstraint10( (Expr10ViewItemImpl) expr );
        }
        if (expr instanceof Expr11ViewItemImpl ) {
            return new SingleConstraint11( (Expr11ViewItemImpl) expr );
        }
        if (expr instanceof Expr12ViewItemImpl ) {
            return new SingleConstraint12( (Expr12ViewItemImpl) expr );
        }
        if (expr instanceof Expr13ViewItemImpl ) {
            return new SingleConstraint13( (Expr13ViewItemImpl) expr );
        }
        return null;
    }

    private static NamedConsequenceImpl createNamedConsequence( Map<String, Consequence> consequences, Consequence consequence ) {
        if (consequence == null) {
            return null;
        }
        String name = generateName("consequence");
        consequences.put(name, consequence);
        return new NamedConsequenceImpl( name, consequence.isBreaking() );
    }

    private static Condition viewItem2Condition( ViewItem viewItem, Condition condition, BuildContext ctx ) {
        if ( viewItem instanceof AbstractExprViewItem ) {
            ( (PatternImpl) condition ).addWatchedProps( (( AbstractExprViewItem ) viewItem).getWatchedProps() );
        }

        if ( viewItem instanceof Expr1ViewItemImpl ) {
            Expr1ViewItemImpl expr = (Expr1ViewItemImpl)viewItem;
            if (expr.getPredicate() != null) {
                ( (PatternImpl) condition ).addConstraint( new SingleConstraint1( expr ) );
            }
            return condition;
        }

        if ( viewItem instanceof ExprNViewItem ) {
            ( (PatternImpl) condition ).addConstraint( SingleConstraint.createConstraint( ( ExprNViewItem ) viewItem ) );
            return condition;
        }

        if (viewItem instanceof TemporalExprViewItem) {
            TemporalExprViewItem expr = (TemporalExprViewItem)viewItem;
            ( (PatternImpl) condition ).addConstraint( TemporalConstraint.createTemporalConstraint( expr ) );
            return condition;
        }

        if ( viewItem instanceof AccumulateExprViewItem) {
            AccumulateExprViewItem acc = (AccumulateExprViewItem)viewItem;

            for ( AccumulateFunction accFunc : acc.getAccumulateFunctions()) {
                ctx.usedVars.add(accFunc.getResult());
            }

            Condition newCondition;
            if (acc.getExpr() instanceof InputViewItem) {
                newCondition = condition;
            } else if (acc.getExpr() instanceof Binding) {
                Binding binding = (( Binding ) acc.getExpr());
                PatternImpl bindingPattern = condition instanceof PatternImpl && (( PatternImpl<?> ) condition).getPatternVariable() == binding.getInputVariable() ?
                    ( PatternImpl<?> ) condition :
                    new PatternImpl( binding.getInputVariable() );
                bindingPattern.addBinding( binding );
                newCondition = bindingPattern;
                ctx.usedVars.add( binding.getBoundVariable() );
            } else {
                newCondition = viewItem2Condition(acc.getExpr(), condition, ctx);
            }

            return new AccumulatePatternImpl(newCondition, acc.getAccumulateFunctions());
        }

        if ( viewItem instanceof CombinedExprViewItem ) {
            CombinedExprViewItem combined = (CombinedExprViewItem) viewItem;
            CompositePatterns patterns = viewItems2Condition( new BuildContext( ctx, combined.getExpressions() ), combined.getType(), false );
            return patterns.getSubConditions().size() == 1 ? patterns.getSubConditions().get(0) : patterns;
        }

        throw new UnsupportedOperationException( "Unknown ViewItem: " + viewItem );
    }

    private static class ConditionComparator implements Comparator<Condition> {

        private static final ConditionComparator INSTANCE = new ConditionComparator();

        @Override
        public int compare( Condition c1, Condition c2 ) {
            if (c1 instanceof Pattern && c2 instanceof Pattern) {
                Pattern p1 = (( Pattern ) c1);
                Pattern p2 = (( Pattern ) c2);
                if (p1.getPatternVariable() != null && p1.getPatternVariable() == getSourceVariable( p2 )) {
                    return -1;
                }
                if (p2.getPatternVariable() != null && p2.getPatternVariable() == getSourceVariable( p1 )) {
                    return 1;
                }
            }
            return 0;
        }

        private static Variable getSourceVariable( Pattern pattern) {
            Variable source = pattern.getPatternVariable();
            if (source instanceof Declaration) {
                DeclarationSource declarationSource = (( Declaration ) source).getSource();
                if (declarationSource instanceof From) {
                    return (( From ) declarationSource).getVariable();
                }
            }
            return null;
        }
    }

    private static class BuildContext {
        final List<RuleItem> ruleItems;
        final Map<Variable<?>, InputViewItemImpl<?>> inputs;
        final Set<Variable<?>> usedVars;
        final Set<Variable<?>> boundVars;
        final Map<Variable<?>, List<Binding>> bindings;
        final BuildContext parent;

        final Set<Variable<?>> existentialVars = new HashSet<>();

        boolean isQuery = false;

        BuildContext( RuleItemBuilder<?>[] viewItemBuilders ) {
            this( null, Stream.of( viewItemBuilders ).map( RuleItemBuilder::get ).collect( toList() ), new LinkedHashMap<>(),
                    new HashSet<>(), new HashSet<>(), new HashMap<>(), false );
        }

        BuildContext( BuildContext orignalContext, ViewItem[] view ) {
            this( orignalContext, Arrays.asList( view ), orignalContext.inputs, orignalContext.usedVars, orignalContext.boundVars, orignalContext.bindings, orignalContext.isQuery );
        }

        BuildContext( BuildContext orignalContext, Map<Variable<?>, InputViewItemImpl<?>> inputs ) {
            this( orignalContext, orignalContext.ruleItems, inputs, orignalContext.usedVars, orignalContext.boundVars, orignalContext.bindings, orignalContext.isQuery );
        }

        BuildContext( BuildContext orignalContext, ViewItem[] view, Map<Variable<?>, InputViewItemImpl<?>> inputs ) {
            this( orignalContext, Arrays.asList( view ), inputs, orignalContext.usedVars, orignalContext.boundVars, orignalContext.bindings, orignalContext.isQuery );
        }

        BuildContext( BuildContext parent, List<RuleItem> ruleItems, Map<Variable<?>, InputViewItemImpl<?>> inputs, Set<Variable<?>> usedVars,
                      Set<Variable<?>> boundVars, Map<Variable<?>, List<Binding>> bindings, boolean isQuery ) {
            this.parent = parent;
            this.ruleItems = ruleItems;
            this.inputs = inputs;
            this.usedVars = usedVars;
            this.boundVars = boundVars;
            this.bindings = bindings;
            this.isQuery = isQuery;
        }

        void addBinding(Binding bindViewItem) {
            boundVars.add(bindViewItem.getBoundVariable());
            bindings.computeIfAbsent( bindViewItem.getInputVariable(), v -> new ArrayList<>() ).add( bindViewItem );
        }

        BuildContext getRootParent() {
            return parent == null ? this : parent.getRootParent();
        }

        void addExistentialVar(Variable<?> var) {
            getRootParent().existentialVars.add( var );
        }

        boolean isExistentialVar(Variable<?> var) {
            return getRootParent().existentialVars.contains( var );
        }
    }
}
