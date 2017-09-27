package org.drools.model.impl;

import org.drools.model.*;
import org.drools.model.Condition.Type;
import org.drools.model.consequences.ConditionalNamedConsequenceImpl;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.TemporalConstraint;
import org.drools.model.patterns.*;
import org.drools.model.view.*;
import org.drools.model.view.OOPathViewItem.OOPathChunk;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.drools.model.DSL.input;
import static org.drools.model.constraints.AbstractSingleConstraint.fromExpr;
import static org.drools.model.impl.NamesGenerator.generateName;

public class ViewBuilder {

    private ViewBuilder() { }

    public static CompositePatterns viewItems2Patterns( RuleItemBuilder[] viewItemBuilders ) {
        List<RuleItem> ruleItems = Stream.of( viewItemBuilders ).map( RuleItemBuilder::get ).collect( toList() );
        Map<Variable<?>, InputViewItemImpl<?>> inputs = new LinkedHashMap<>();
        Set<Variable<?>> usedVars = new HashSet<>();
        CompositePatterns view = viewItems2Condition( ruleItems, inputs, usedVars, Type.AND, true );
        ensureVariablesDeclarationInView(view, inputs, usedVars);
        return view;
    }

    private static void ensureVariablesDeclarationInView(CompositePatterns view, Map<Variable<?>, InputViewItemImpl<?>> inputs, Set<Variable<?>> usedVars) {
        if ( inputs.size() > usedVars.size() ) {
            inputs.keySet().removeAll( usedVars );
            int i = 0;
            for ( Map.Entry<Variable<?>, InputViewItemImpl<?>> entry : inputs.entrySet() ) {
                view.addCondition( i++, new PatternImpl( entry.getKey(), SingleConstraint.EMPTY, entry.getValue().getDataSourceDefinition() ) );
                usedVars.add( entry.getKey() );
            }
        }

        view.ensureVariablesDeclarationInView();
    }

    private static CompositePatterns viewItems2Condition(List<RuleItem> ruleItems, Map<Variable<?>, InputViewItemImpl<?>> inputs,
                                                        Set<Variable<?>> usedVars, Condition.Type type, boolean topLevel) {
        List<Condition> conditions = new ArrayList<>();
        Map<Variable<?>, Condition> conditionMap = new HashMap<>();
        Map<String, Consequence> consequences = topLevel ? new LinkedHashMap<>() : null;
        Iterator<RuleItem> ruleItemIterator = ruleItems.iterator();

        while (ruleItemIterator.hasNext()) {
            RuleItem ruleItem = ruleItemIterator.next();

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
                conditions.add( viewItems2Condition( Arrays.asList( combined.getExpressions() ), inputs, usedVars, combined.getType(), false ) );
                continue;
            }

            if ( viewItem instanceof QueryCallViewItem ) {
                QueryCallViewItem query = ( (QueryCallViewItem) viewItem );
                for ( Argument arg : query.getArguments()) {
                    if (arg instanceof Variable) {
                        usedVars.add( ( (Variable) arg ));
                    }
                }
                conditions.add( new QueryCallPattern( query ) );
                continue;
            }

            Variable<?> patterVariable = viewItem.getFirstVariable();
            if ( viewItem instanceof InputViewItemImpl ) {
                inputs.put( patterVariable, (InputViewItemImpl) viewItem );
                continue;
            }

            if ( viewItem instanceof SetViewItem ) {
                SetViewItem setViewItem = (SetViewItem) viewItem;
                Pattern pattern = setViewItem.isMultivalue() ?
                                  new InvokerMultiValuePatternImpl( DataSourceDefinitionImpl.DEFAULT,
                                                                    setViewItem.getInvokedFunction(),
                                                                    patterVariable,
                                                                    setViewItem.getInputVariables() ) :
                                  new InvokerSingleValuePatternImpl( DataSourceDefinitionImpl.DEFAULT,
                                                                     setViewItem.getInvokedFunction(),
                                                                     patterVariable,
                                                                     setViewItem.getInputVariables() );
                conditionMap.put( patterVariable, pattern );
                conditions.add( pattern );
                continue;
            }

            usedVars.add( patterVariable );
            Condition condition;
            if ( type == Type.AND ) {
                condition = conditionMap.get( patterVariable );
                if ( condition == null ) {
                    condition = new PatternImpl( patterVariable, SingleConstraint.EMPTY, getDataSourceDefinition( inputs, patterVariable ) );
                    conditions.add( condition );
                    conditionMap.put( patterVariable, condition );
                }
            } else {
                condition = new PatternImpl( patterVariable, SingleConstraint.EMPTY, getDataSourceDefinition( inputs, patterVariable ) );
                conditions.add( condition );
            }

            addInputFromVariableSource( inputs, patterVariable );

            if ( viewItem instanceof AbstractExprViewItem && !( (AbstractExprViewItem) viewItem ).isQueryExpression() ) {
                for (Variable var : viewItem.getVariables()) {
                    if (var.isFact()) {
                        inputs.putIfAbsent( var, (InputViewItemImpl) input( var ) );
                    }
                }
            }

            Condition modifiedPattern = viewItem2Condition( viewItem, condition, usedVars, inputs );
            conditions.set( conditions.indexOf( condition ), modifiedPattern );
            if (type == Type.AND) {
                conditionMap.put( patterVariable, modifiedPattern );
            }
        }

        return new CompositePatterns( type, conditions, usedVars, consequences );
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
        SingleConstraint constraint = cond.getExpr() == null ?
                null :
                cond.getExpr() instanceof Expr1ViewItemImpl ?
                        new SingleConstraint1( (Expr1ViewItemImpl) cond.getExpr() ):
                        new SingleConstraint2( (Expr2ViewItemImpl) cond.getExpr() );
        return new ConditionalNamedConsequenceImpl( constraint,
                                                    createNamedConsequence( consequences, cond.getThen() ),
                                                    cond.getElse() != null ? createConditionalNamedConsequence( consequences, cond.getElse() ) : null );
    }

    private static NamedConsequenceImpl createNamedConsequence( Map<String, Consequence> consequences, Consequence consequence ) {
        if (consequence == null) {
            return null;
        }
        String name = generateName("consequence");
        consequences.put(name, consequence);
        return new NamedConsequenceImpl( name, consequence.isBreaking() );
    }

    private static DataSourceDefinition getDataSourceDefinition( Map<Variable<?>, InputViewItemImpl<?>> inputs, Variable var ) {
        InputViewItemImpl input = inputs.get( var );
        return input != null ? input.getDataSourceDefinition() : DataSourceDefinitionImpl.DEFAULT;
    }

    private static Condition viewItem2Condition( ViewItem viewItem, Condition condition, Set<Variable<?>> usedVars, Map<Variable<?>, InputViewItemImpl<?>> inputs ) {
        if ( viewItem instanceof Expr1ViewItemImpl ) {
            Expr1ViewItemImpl expr = (Expr1ViewItemImpl)viewItem;
            if (expr.getPredicate() != null) {
                ( (PatternImpl) condition ).addConstraint( new SingleConstraint1( expr ) );
            }
            return condition;
        }

        if ( viewItem instanceof Expr2ViewItemImpl ) {
            Expr2ViewItemImpl expr = (Expr2ViewItemImpl)viewItem;
            ( (PatternImpl) condition ).addConstraint( new SingleConstraint2( expr ) );
            return condition;
        }

        if (viewItem instanceof TemporalExprViewItem) {
            TemporalExprViewItem expr = (TemporalExprViewItem)viewItem;
            ( (PatternImpl) condition ).addConstraint( new TemporalConstraint( expr ) );
            return condition;
        }

        if ( viewItem instanceof AccumulateExprViewItem) {
            AccumulateExprViewItem acc = (AccumulateExprViewItem)viewItem;
            for ( AccumulateFunction accFunc : acc.getFunctions()) {
                usedVars.add(accFunc.getVariable());
            }
            return new AccumulatePatternImpl( (Pattern) viewItem2Condition( acc.getExpr(), condition, usedVars, inputs ), acc.getFunctions() );
        }

        if ( viewItem instanceof OOPathViewItem) {
            OOPathViewItem<?,?> oopath = ( (OOPathViewItem) viewItem );
            if (oopath.getChunks().size() > 1) {
                throw new UnsupportedOperationException();
            }
            OOPathChunk chunk = oopath.getChunks().get( 0 );
            for (Variable var : chunk.getExpr().getVariables()) {
                usedVars.add(var);
            }
            ( (PatternImpl) condition ).addConstraint( fromExpr( chunk.getExpr() ) );
            OOPathImpl oopathPattern = new OOPathImpl( oopath.getSource(), oopath.getChunks() );
            oopathPattern.setFirstCondition( condition );
            return oopathPattern;
        }

        if ( viewItem instanceof ExistentialExprViewItem) {
            ExistentialExprViewItem existential = ( (ExistentialExprViewItem) viewItem );
            return new ExistentialPatternImpl( viewItem2Condition( existential.getExpression(), condition, usedVars, inputs ), existential.getType() );
        }

        if ( viewItem instanceof CombinedExprViewItem ) {
            CombinedExprViewItem combined = (CombinedExprViewItem) viewItem;
            CompositePatterns patterns = viewItems2Condition( Arrays.asList( combined.getExpressions() ), inputs, usedVars, combined.getType(), false );
            return patterns.getSubConditions().size() == 1 ? patterns.getSubConditions().get(0) : patterns;
        }

        throw new UnsupportedOperationException( "Unknown ViewItem: " + viewItem );
    }
}
