package org.drools.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.drools.model.AccumulateFunction;
import org.drools.model.Argument;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Constraint;
import org.drools.model.DataSourceDefinition;
import org.drools.model.Pattern;
import org.drools.model.Variable;
import org.drools.model.constraints.SingleConstraint1;
import org.drools.model.constraints.SingleConstraint2;
import org.drools.model.constraints.TemporalConstraint;
import org.drools.model.patterns.AccumulatePatternImpl;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.ExistentialPatternImpl;
import org.drools.model.patterns.InvokerMultiValuePatternImpl;
import org.drools.model.patterns.InvokerSingleValuePatternImpl;
import org.drools.model.patterns.OOPathImpl;
import org.drools.model.patterns.PatternImpl;
import org.drools.model.patterns.QueryCallPattern;
import org.drools.model.view.AbstractExprViewItem;
import org.drools.model.view.AccumulateExprViewItem;
import org.drools.model.view.CombinedExprViewItem;
import org.drools.model.view.ExistentialExprViewItem;
import org.drools.model.view.Expr1ViewItemImpl;
import org.drools.model.view.Expr2ViewItemImpl;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.InputViewItemImpl;
import org.drools.model.view.OOPathViewItem;
import org.drools.model.view.OOPathViewItem.OOPathChunk;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.SetViewItem;
import org.drools.model.view.TemporalExprViewItem;
import org.drools.model.view.ViewItem;
import org.drools.model.view.ViewItemBuilder;

import static java.util.stream.Collectors.toList;
import static org.drools.model.DSL.input;
import static org.drools.model.constraints.AbstractSingleConstraint.fromExpr;

public class ViewBuilder {

    private ViewBuilder() { }

    public static CompositePatterns viewItems2Patterns( ViewItemBuilder[] viewItemBuilders ) {
        if (viewItemBuilders.length == 1 && viewItemBuilders[0] instanceof ExprViewItem && ((ExprViewItem) viewItemBuilders[0]).getType() == Type.AND) {
            return viewItems2Condition( Arrays.asList(((CombinedExprViewItem) viewItemBuilders[0]).getExpressions()), new HashMap<>(), new HashSet<>(), Type.AND, true );
        }
        List<ViewItem> viewItems = Stream.of( viewItemBuilders ).map( ViewItemBuilder::get ).collect( toList() );
        return viewItems2Condition( viewItems, new HashMap<>(), new HashSet<>(), Type.AND, true );
    }

    public static CompositePatterns viewItems2Condition(List<ViewItem> viewItems, Map<Variable<?>, InputViewItemImpl<?>> inputs,
                                                        Set<Variable<?>> usedVars, Condition.Type type, boolean topLevel) {
        List<Condition> conditions = new ArrayList<>();
        Map<Variable<?>, Condition> conditionMap = new HashMap<>();
        for (ViewItem viewItem : viewItems) {
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
                    condition = new PatternImpl( patterVariable, Constraint.EMPTY, getDataSourceDefinition( inputs, patterVariable ) );
                    conditions.add( condition );
                    conditionMap.put( patterVariable, condition );
                }
            } else {
                condition = new PatternImpl( patterVariable, Constraint.EMPTY, getDataSourceDefinition( inputs, patterVariable ) );
                conditions.add( condition );
            }

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

        CompositePatterns condition = new CompositePatterns( type, conditions, usedVars );
        if ( type == Type.AND ) {
            if ( topLevel && inputs.size() > usedVars.size() ) {
                inputs.keySet().removeAll( usedVars );
                for ( Map.Entry<Variable<?>, InputViewItemImpl<?>> entry : inputs.entrySet() ) {
                    conditions.add( 0, new PatternImpl( entry.getKey(), Constraint.EMPTY, entry.getValue().getDataSourceDefinition() ) );
                    usedVars.add( entry.getKey() );
                }
            }
        }
        return condition;
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
