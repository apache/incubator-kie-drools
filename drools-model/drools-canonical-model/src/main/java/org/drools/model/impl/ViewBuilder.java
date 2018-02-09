package org.drools.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import org.drools.model.Pattern;
import org.drools.model.RuleItem;
import org.drools.model.RuleItemBuilder;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;
import org.drools.model.consequences.ConditionalNamedConsequenceImpl;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.SingleConstraint3;
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
import org.drools.model.view.Expr1ViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.Expr3ViewItemImpl;
import org.drools.model.view.ExprNViewItem;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.FixedValueItem;
import org.drools.model.view.InputViewItem;
import org.drools.model.view.InputViewItemImpl;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.ViewItem;

import static java.util.stream.Collectors.toList;

import static org.drools.model.DSL.input;
import static org.drools.model.impl.NamesGenerator.generateName;

public class ViewBuilder {

    private ViewBuilder() { }

    public static CompositePatterns viewItems2Patterns( RuleItemBuilder[] viewItemBuilders ) {
        BuildContext ctx = new BuildContext( viewItemBuilders );
        return ensureVariablesDeclarationInView(viewItems2Condition( ctx, Type.AND, true ), ctx);
    }

    private static CompositePatterns ensureVariablesDeclarationInView(CompositePatterns view, BuildContext ctx) {
        ctx.inputs.keySet().removeAll(ctx.usedVars);
        int i = 0;
        for (Map.Entry<Variable<?>, InputViewItemImpl<?>> entry : ctx.inputs.entrySet()) {
            view.addCondition(i++, new PatternImpl(entry.getKey(), SingleConstraint.TRUE ));
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
                conditions.add( viewItems2Condition( new BuildContext( ctx, combined.getExpressions(), scopedInputs ), combined.getType(), false ) );
                continue;
            }

            if ( viewItem instanceof QueryCallViewItem ) {
                QueryCallViewItem query = ( (QueryCallViewItem) viewItem );
                for ( Argument arg : query.getArguments()) {
                    if (arg instanceof Variable) {
                        ctx.usedVars.add( ( (Variable) arg ));
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
                    ctx.usedVars.add( bindViewItem.getInputVariable() );
                    pattern.addAllInputVariables(bindViewItem.getInputVariables());
                    conditions.add( pattern );
                    conditionMap.put( bindViewItem.getInputVariable(), pattern );
                }
                pattern.addBinding( bindViewItem );
                ctx.usedVars.add(viewItem.getFirstVariable());
                ctx.addBinding(bindViewItem);
                continue;
            }

            Variable<?> patterVariable = findPatterVariable( viewItem, scopedInputs.keySet() );

            if ( viewItem instanceof InputViewItemImpl ) {
                scopedInputs.put( patterVariable, (InputViewItemImpl) viewItem );
                Condition condition = new PatternImpl( patterVariable, SingleConstraint.TRUE );
                conditions.add( condition );
                conditionMap.put( patterVariable, condition );
                ctx.usedVars.add( patterVariable );
                continue;
            }

            if ( viewItem instanceof ExistentialExprViewItem ) {
                ExistentialExprViewItem existential = ( (ExistentialExprViewItem) viewItem );
                if (patterVariable != null && !existential.isQueryExpression()) {
                    registerInputsFromViewItem( existential.getExpression(), conditionMap, scopedInputs, patterVariable );
                }
                Condition condition = new PatternImpl( patterVariable, SingleConstraint.TRUE, ctx.bindings.get(patterVariable) );
                conditions.add( new ExistentialPatternImpl( viewItem2Condition( existential.getExpression(), condition, new BuildContext( ctx, new LinkedHashMap<>() ) ), existential.getType() ) );
                continue;
            }

            if ( ruleItem instanceof ExprViewItem && ctx.boundVars.contains( patterVariable ) ) {
                conditions.add( new EvalImpl( createConstraint( (ExprViewItem) ruleItem ) ) );
                continue;
            }

            ctx.usedVars.add( patterVariable );
            Condition condition;
            if ( type == Type.AND ) {
                condition = conditionMap.get( patterVariable );
                if ( condition == null ) {
                    condition = new PatternImpl( patterVariable, SingleConstraint.TRUE, ctx.bindings.get(patterVariable) );
                    conditions.add( condition );
                    if (!(viewItem instanceof AccumulateExprViewItem)) {
                        conditionMap.put( patterVariable, condition );
                    }
                    scopedInputs.putIfAbsent( patterVariable, (InputViewItemImpl) input( patterVariable ) );
                }
            } else {
                condition = new PatternImpl( patterVariable, SingleConstraint.TRUE );
                conditions.add( condition );
            }

            addInputFromVariableSource( scopedInputs, patterVariable );
            registerInputsFromViewItem( viewItem, conditionMap, scopedInputs, null );

            Condition modifiedPattern = viewItem2Condition( viewItem, condition, new BuildContext( ctx, scopedInputs ) );
            conditions.set( conditions.indexOf( condition ), modifiedPattern );

            if ( type == Type.AND && !(viewItem instanceof AccumulateExprViewItem) ) {
                conditionMap.put( patterVariable, modifiedPattern );
            }
        }


        Optional<PatternImpl> patternImpl = Optional.empty();
        for(Condition c : conditions) {
            if(c instanceof AccumulatePatternImpl) {
                final AccumulatePatternImpl accumulatePattern = (AccumulatePatternImpl) c;
                patternImpl = findPatternImplSource(accumulatePattern, conditions);
                patternImpl.ifPresent(accumulatePattern::setPattern);
            }
        }
        patternImpl.ifPresent(conditions::remove);

        return new CompositePatterns( type, conditions, ctx.usedVars, consequences );
    }

    private static void registerInputsFromViewItem( ViewItem viewItem, Map<Variable<?>, Condition> conditionMap, Map<Variable<?>, InputViewItemImpl<?>> scopedInputs, Variable<?> existentialVar ) {
        if ( viewItem instanceof AbstractExprViewItem && !( (AbstractExprViewItem) viewItem ).isQueryExpression() ) {
            for (Variable var : viewItem.getVariables()) {
                if (var.isFact() && !conditionMap.containsKey( var ) && var != existentialVar) {
                    scopedInputs.putIfAbsent( var, (InputViewItemImpl) input( var ) );
                }
            }
        }
    }

    private static Variable<?> findPatterVariable( ViewItem viewItem, Set<Variable<?>> vars ) {
        Variable<?> patternVariable = viewItem.getFirstVariable();
        if (viewItem instanceof Expr1ViewItem || !vars.contains( patternVariable )) {
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

    private static Optional<PatternImpl> findPatternImplSource(AccumulatePatternImpl accumulatePattern, List<Condition> conditions) {
        final Variable source = accumulatePattern.getAccumulateFunctions()[0].getSource();

        for (Condition subCondition : conditions) {
            if (subCondition instanceof PatternImpl) {
                PatternImpl patternImpl = (PatternImpl) subCondition;

                boolean isSource =  patternImpl
                        .getBindings()
                        .stream()
                        .anyMatch(b -> (b instanceof Binding) && ((Binding) b).getBoundVariable().equals(source));
                if(isSource) {
                    return Optional.of(patternImpl);
                }

            }
        }
        return Optional.empty();
    }

    private static void addInputFromVariableSource( Map<Variable<?>, InputViewItemImpl<?>> inputs, Variable<?> patterVariable ) {
        if ( patterVariable instanceof Declaration ) {
            Declaration declaration = (( Declaration ) patterVariable);
            if ( declaration.getSource() instanceof From ) {
                Variable var = (( From ) declaration.getSource()).getVariable();
                addInputFromVariableSource( inputs, var );
                if (var.isFact()) {
                    inputs.putIfAbsent( var, (InputViewItemImpl) input( var ) );
                }
            }
        }
    }

    private static ConditionalNamedConsequenceImpl createConditionalNamedConsequence(Map<String, Consequence> consequences, ConditionalConsequence cond) {
        return new ConditionalNamedConsequenceImpl( createConstraint( cond.getExpr() ),
                                                    createNamedConsequence( consequences, cond.getThen() ),
                                                    cond.getElse() != null ? createConditionalNamedConsequence( consequences, cond.getElse() ) : null );
    }

    private static SingleConstraint createConstraint( ExprViewItem expr ) {
        if (expr instanceof Expr1ViewItemImpl) {
            return new SingleConstraint1( (Expr1ViewItemImpl) expr );
        }
        if (expr instanceof Expr2ViewItemImpl) {
            return new SingleConstraint2( (Expr2ViewItemImpl) expr );
        }
        if (expr instanceof Expr3ViewItemImpl) {
            return new SingleConstraint3( (Expr3ViewItemImpl) expr );
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
            ( (PatternImpl) condition ).addConstraint( new TemporalConstraint( expr ) );
            return condition;
        }

        if ( viewItem instanceof AccumulateExprViewItem) {
            AccumulateExprViewItem acc = (AccumulateExprViewItem)viewItem;

            for ( AccumulateFunction accFunc : acc.getAccumulateFunctions()) {
                ctx.usedVars.add(accFunc.getVariable());
            }

            final Condition newCondition = acc.getExpr() instanceof InputViewItem ? condition : viewItem2Condition(acc.getExpr(), condition, ctx);
            if (newCondition instanceof Pattern) {
                return new AccumulatePatternImpl((Pattern) newCondition, Optional.empty(), acc.getAccumulateFunctions());
            } else if (newCondition instanceof CompositePatterns) {
                return new AccumulatePatternImpl(null, Optional.of(newCondition), acc.getAccumulateFunctions());
            } else {
                throw new RuntimeException("Unknown pattern");
            }
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
                if (p1.getPatternVariable() == getSourceVariable( p2 )) {
                    return -1;
                }
                if (p2.getPatternVariable() == getSourceVariable( p1 )) {
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

        BuildContext( RuleItemBuilder[] viewItemBuilders ) {
            this( Stream.of( viewItemBuilders ).map( RuleItemBuilder::get ).collect( toList() ), new LinkedHashMap<>(),
                    new HashSet<>(), new HashSet<>(), new HashMap<>() );
        }

        BuildContext( BuildContext orignalContext, ViewItem[] view ) {
            this( Arrays.asList( view ), orignalContext.inputs, orignalContext.usedVars, orignalContext.boundVars, orignalContext.bindings );
        }

        BuildContext( BuildContext orignalContext, Map<Variable<?>, InputViewItemImpl<?>> inputs ) {
            this( orignalContext.ruleItems, inputs, orignalContext.usedVars, orignalContext.boundVars, orignalContext.bindings );
        }

        BuildContext( BuildContext orignalContext, ViewItem[] view, Map<Variable<?>, InputViewItemImpl<?>> inputs ) {
            this( Arrays.asList( view ), inputs, orignalContext.usedVars, orignalContext.boundVars, orignalContext.bindings );
        }

        BuildContext( List<RuleItem> ruleItems, Map<Variable<?>, InputViewItemImpl<?>> inputs, Set<Variable<?>> usedVars,
                      Set<Variable<?>> boundVars, Map<Variable<?>, List<Binding>> bindings ) {
            this.ruleItems = ruleItems;
            this.inputs = inputs;
            this.usedVars = usedVars;
            this.boundVars = boundVars;
            this.bindings = bindings;
        }

        void addBinding(Binding bindViewItem) {
            boundVars.add(bindViewItem.getBoundVariable());
            bindings.computeIfAbsent( bindViewItem.getInputVariable(), v -> new ArrayList<>() ).add( bindViewItem );
        }
    }
}
