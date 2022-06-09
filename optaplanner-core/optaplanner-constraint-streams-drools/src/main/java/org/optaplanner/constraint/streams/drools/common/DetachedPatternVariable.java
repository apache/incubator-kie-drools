package org.optaplanner.constraint.streams.drools.common;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.common.tri.DefaultTriJoiner;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.JoinerType;

/**
 * Represents a single variable that has no pattern on which to be bound.
 * Such a variable is often the result of a Drools accumulate call.
 *
 * <p>
 * Consider the following simple univariate rule, in the equivalent DRL:
 *
 * <pre>
 * {@code
 *  rule "Simple univariate rule"
 *  when
 *      accumulate(
 *          ...,
 *          $count: count()
 *      )
 *      $a: Something($b: someField, someOtherField > $count)
 *  then
 *      // Do something.
 *  end
 * }
 * </pre>
 * <p>
 * In this rule, variable "a" would be represented by {@link DirectPatternVariable}.
 * Variable "b" would be represented by {@link IndirectPatternVariable}.
 * Variable "count" would be represented by this class and passed to the first variable's pattern's filter expression.
 *
 * <p>
 * Therefore most of its operations will throw {@link UnsupportedOperationException}, as you can not really do anything
 * with the variable.
 * It is only useful as an auxiliary variable in bindings or expressions on another {@link DirectPatternVariable} or
 * {@link IndirectPatternVariable}.
 *
 * <p>
 * The pattern that produces the detached variable will be included in prerequisites for another variable.
 * Therefore {@link #build()} of this class returns an empty result.
 * 
 * @param <A> generic type of the variable
 * @param <PatternVar_>> generic type of the pattern variable, has no effect and exists only to satisfy the interface
 */
final class DetachedPatternVariable<A, PatternVar_>
        implements PatternVariable<A, PatternVar_, DetachedPatternVariable<A, PatternVar_>> {

    private final Variable<A> primaryVariable;

    DetachedPatternVariable(Variable<A> variable) {
        this.primaryVariable = Objects.requireNonNull(variable);
    }

    @Override
    public Variable<A> getPrimaryVariable() {
        return primaryVariable;
    }

    @Override
    public List<ViewItem<?>> getPrerequisiteExpressions() {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public List<ViewItem<?>> getDependentExpressions() {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public DetachedPatternVariable<A, PatternVar_> filter(Predicate<A> predicate) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <LeftJoinVar_> DetachedPatternVariable<A, PatternVar_> filter(BiPredicate<LeftJoinVar_, A> predicate,
            Variable<LeftJoinVar_> leftJoinVariable) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_> DetachedPatternVariable<A, PatternVar_> filter(
            TriPredicate<LeftJoinVarA_, LeftJoinVarB_, A> predicate, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> DetachedPatternVariable<A, PatternVar_> filter(
            QuadPredicate<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> predicate, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB, Variable<LeftJoinVarC_> leftJoinVariableC) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <LeftJoinVar_> PatternVariable<A, PatternVar_, DetachedPatternVariable<A, PatternVar_>> filterForJoin(
            Variable<LeftJoinVar_> leftJoinVar, DefaultBiJoiner<LeftJoinVar_, A> joiner, JoinerType joinerType,
            int mappingIndex) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_> PatternVariable<A, PatternVar_, DetachedPatternVariable<A, PatternVar_>>
            filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA, Variable<LeftJoinVarB_> leftJoinVarB,
                    DefaultTriJoiner<LeftJoinVarA_, LeftJoinVarB_, A> joiner, JoinerType joinerType, int mappingIndex) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_>
            PatternVariable<A, PatternVar_, DetachedPatternVariable<A, PatternVar_>>
            filterForJoin(Variable<LeftJoinVarA_> leftJoinVarA, Variable<LeftJoinVarB_> leftJoinVarB,
                    Variable<LeftJoinVarC_> leftJoinVarC,
                    DefaultQuadJoiner<LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, A> joiner, JoinerType joinerType,
                    int mappingIndex) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <BoundVar_> DetachedPatternVariable<A, PatternVar_> bind(Variable<BoundVar_> boundVariable,
            Function<A, BoundVar_> bindingFunction) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <BoundVar_, LeftJoinVar_> DetachedPatternVariable<A, PatternVar_> bind(Variable<BoundVar_> boundVariable,
            Variable<LeftJoinVar_> leftJoinVariable, BiFunction<A, LeftJoinVar_, BoundVar_> bindingFunction) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <BoundVar_, LeftJoinVarA_, LeftJoinVarB_> DetachedPatternVariable<A, PatternVar_> bind(
            Variable<BoundVar_> boundVariable, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB,
            TriFunction<A, LeftJoinVarA_, LeftJoinVarB_, BoundVar_> bindingFunction) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public <BoundVar_, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_> DetachedPatternVariable<A, PatternVar_> bind(
            Variable<BoundVar_> boundVariable, Variable<LeftJoinVarA_> leftJoinVariableA,
            Variable<LeftJoinVarB_> leftJoinVariableB, Variable<LeftJoinVarC_> leftJoinVariableC,
            QuadFunction<A, LeftJoinVarA_, LeftJoinVarB_, LeftJoinVarC_, BoundVar_> bindingFunction) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public DetachedPatternVariable<A, PatternVar_> addDependentExpression(ViewItem<?> expression) {
        throw new UnsupportedOperationException("Impossible state: Variable (" + primaryVariable + ") is detached.");
    }

    @Override
    public List<ViewItem<?>> build() {
        return Collections.emptyList(); // This variable has no related patterns.
    }
}
