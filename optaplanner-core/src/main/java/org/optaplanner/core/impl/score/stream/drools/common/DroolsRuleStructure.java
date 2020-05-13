/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.drools.model.DSL.from;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.DSL;
import org.drools.model.DeclarationSource;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.view.ViewItem;
import org.drools.model.view.ViewItemBuilder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;

/**
 * Represents the left-hand side of a Drools rule.
 *
 * @param <PatternVar> type of the variable of the primary pattern (see {@link #getPrimaryPatternBuilder()})
 */
public abstract class DroolsRuleStructure<PatternVar> {

    private final LongSupplier variableIdSupplier;

    protected DroolsRuleStructure(LongSupplier variableIdSupplier) {
        this.variableIdSupplier = variableIdSupplier;
    }

    /**
     * Declare a new {@link Variable} in this rule, with a given name and no declared source. Delegates to
     * {@link DSL#declarationOf(Class, String)}.
     * Creating variables via this method guarantees unique variable name within the context of a rule through the use
     * of {@link DroolsConstraintFactory#getVariableIdSupplier()}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     *        amount of time looking up applicable instances of that variable, as it has to traverse instances of all types in
     *        the working memory. Therefore, it is desirable to be as specific as possible.
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     *        numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     *        rule.
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public final <X> Variable<? extends X> createVariable(Class<X> clz, String name) {
        return DSL.declarationOf(clz, decorateVariableName(name));
    }

    /**
     * Declare a new {@link Variable} in this rule, with a given name and a declaration source.
     * Delegates to {@link DSL#declarationOf(Class, String, DeclarationSource)}.
     * Creating variables via this method guarantees unique variable names within the context of a rule through the use
     * of {@link DroolsConstraintFactory#getVariableIdSupplier()}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     *        amount of time looking up applicable instances of that variable, as it has to traverse instances of all types in
     *        the working memory. Therefore, it is desirable to be as specific as possible.
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     *        numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     *        rule.
     * @param source declaration source of the variable
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public final <X> Variable<? extends X> createVariable(Class<X> clz, String name, DeclarationSource source) {
        return DSL.declarationOf(clz, decorateVariableName(name), source);
    }

    private String decorateVariableName(String name) {
        return "$var" + variableIdSupplier.getAsLong() + "_" + name;
    }

    /**
     * Declares a new {@link Object}-typed variable, see {@link #createVariable(Class, String, DeclarationSource)} for
     * details.
     */
    public final <X> Variable<X> createVariable(String name) {
        return (Variable<X>) createVariable(Object.class, name);
    }

    /**
     * Declares a new {@link Object}-typed variable, see {@link #createVariable(Class, String)} for details.
     */
    public final <X> Variable<X> createVariable(String name, DeclarationSource source) {
        return (Variable<X>) createVariable(Object.class, name, source);
    }

    public final List<RuleItemBuilder<?>> finish(ConsequenceBuilder.AbstractValidBuilder<?> consequence) {
        List<ViewItemBuilder<?>> shelved = getShelvedRuleItems();
        List<ViewItemBuilder<?>> prerequisites = getPrerequisites();
        List<ViewItemBuilder<?>> dependents = getDependents();
        List<RuleItemBuilder<?>> result = new ArrayList<>(shelved.size() + prerequisites.size() + dependents.size() + 2);
        result.addAll(shelved);
        result.addAll(prerequisites);
        result.add(getPrimaryPatternBuilder().build());
        result.addAll(dependents);
        result.add(consequence);
        return result;
    }

    public LongSupplier getVariableIdSupplier() {
        return variableIdSupplier;
    }

    /**
     * Primary pattern is the Drools pattern to which operations such as filter and join will be applied.
     * Consider the following example left hand side DRL:
     *
     * <pre>
     *     $person: Person()
     *     $lesson: Lesson($person in people)
     * </pre>
     *
     * Here, Lesson is the primary pattern of this rule.
     * We can use filters to restrict matched Lesson instances, and we can use $person variable in those filters.
     * But the Person() instances themselves are now fixed and the Person pattern can be found in
     * {@link #getPrerequisites()} and no longer modified.
     *
     * <p>
     * The primary pattern is provided as a builder.
     * This is necessary since the patterns are shared and modified between different classes and it would therefore be
     * possible for one class to modify the other's pattern.
     * This way, the pattern is only constructed when necessary, at which point it will no longer be shared.
     *
     * @return never null, builder that will be used to obtain the final version of the primary pattern
     */
    public abstract DroolsPatternBuilder<PatternVar> getPrimaryPatternBuilder();

    /**
     * Patterns that are no longer of any use to the primary pattern, yet are required for the Drools rule to function.
     * Consider the following example left hand side DRL:
     *
     * <pre>
     *     $tuples: Set() from accumulate(...) // This is the shelved pattern.
     *     $person: Person() from $tuples // This is the prerequisite pattern, referencing the shelved pattern.
     *     $lesson: Lesson($person in people) // This is the primary pattern, referencing the prerequisite pattern.
     * </pre>
     *
     * In this example, any further Person filters or joiners will still be applied on the primary pattern.
     * Yet the rule overall would not function properly without also including the shelved pattern.
     *
     * <p>
     * The difference between these and {@link #getPrerequisites()} is that the latter would be folded inside a
     * subsequent accumulate pattern, while shelved patterns (typically representing previous accumulate patterns)
     * wouldn't.
     * Consider the following example left hand side DRL:
     *
     * <pre>
     *     $tuples: Set() from accumulate(...) // This is the original shelved pattern from above.
     *     $otherTuples: Set() from accumulate(
     *          and(
     *              $person: Person() from $tuples, // This is the original prerequisite pattern from above.
     *              $lesson: Lesson($person in people) // This is the original primary pattern from above.
     *          ),
     *          collectCount()
     *     )
     *     $otherPerson: Person() from $otherTuples // This is the new primary pattern.
     * </pre>
     *
     * @return never null, a list of preceding items that are required by the primary pattern.
     */
    public abstract List<ViewItemBuilder<?>> getShelvedRuleItems();

    /**
     * See {@link #getPrimaryPatternBuilder()} for a definition.
     *
     * @return never null, a list of preceding items that are required by the primary pattern
     */
    public abstract List<ViewItemBuilder<?>> getPrerequisites();

    /**
     * Patterns that follow up on the primary pattern, yet are not used for filtering or joining.
     * Consider the following example left hand side DRL:
     *
     * <pre>
     *     $person: Person() // This is the primary pattern.
     *     exists Person(this != $person) // This is the dependent, immutable pattern.
     * </pre>
     *
     * In this example, any further Person filters or joiners will still be applied on the primary pattern.
     * Yet the rule overall would not function properly without also including the dependent pattern.
     *
     * @return never null, a list of subsequent items that are required by the primary pattern
     */
    public abstract List<ViewItemBuilder<?>> getDependents();

    protected List<ViewItemBuilder<?>> mergeShelved(ViewItemBuilder<?>... newShelvedItems) {
        return Stream.concat(getShelvedRuleItems().stream(), Stream.of(newShelvedItems))
                .collect(Collectors.toList());
    }

    protected List<ViewItemBuilder<?>> mergeDependents(ViewItemBuilder<?>... newDependents) {
        return Stream.concat(getDependents().stream(), Stream.of(newDependents))
                .collect(Collectors.toList());
    }

    public <NewA> DroolsUniRuleStructure<NewA, NewA> recollect(Variable<NewA> newA, ViewItem<?> accumulatePattern) {
        DroolsPatternBuilder<NewA> newPrimaryPattern = new DroolsPatternBuilder<>(newA);
        return new DroolsUniRuleStructure<>(newA, newPrimaryPattern, mergeShelved(accumulatePattern),
                emptyList(), getDependents(), getVariableIdSupplier());
    }

    public <NewA> DroolsUniRuleStructure<NewA, NewA> regroup(Variable<Collection<NewA>> newASource,
            PatternDef<Collection<NewA>> collectPattern, ViewItem<?> accumulatePattern) {
        Variable<NewA> newA = createVariable("groupKey", from(newASource));
        DroolsPatternBuilder<NewA> newPrimaryPattern = new DroolsPatternBuilder<>(newA);
        return new DroolsUniRuleStructure<>(newA, newPrimaryPattern, mergeShelved(accumulatePattern),
                singletonList(collectPattern), emptyList(), getVariableIdSupplier());
    }

    public <NewA, NewB> DroolsBiRuleStructure<NewA, NewB, BiTuple<NewA, NewB>> regroupBi(
            Variable<Collection<BiTuple<NewA, NewB>>> newSource,
            PatternDef<Collection<BiTuple<NewA, NewB>>> collectPattern, ViewItem<?> accumulatePattern) {
        Variable<BiTuple<NewA, NewB>> newTuple = (Variable<BiTuple<NewA, NewB>>) createVariable(BiTuple.class, "groupKey",
                from(newSource));
        Variable<NewA> newA = createVariable("newA");
        Variable<NewB> newB = createVariable("newB");
        DroolsPatternBuilder<BiTuple<NewA, NewB>> newPrimaryPattern = new DroolsPatternBuilder<>(newTuple)
                .expand(p -> p.bind(newA, tuple -> tuple.a))
                .expand(p -> p.bind(newB, tuple -> tuple.b));
        return new DroolsBiRuleStructure<>(newA, newB, newPrimaryPattern, mergeShelved(accumulatePattern),
                singletonList(collectPattern), emptyList(), getVariableIdSupplier());
    }

    public <NewA, NewB, NewC> DroolsTriRuleStructure<NewA, NewB, NewC, TriTuple<NewA, NewB, NewC>> regroupBiToTri(
            Variable<Set<TriTuple<NewA, NewB, NewC>>> newSource,
            PatternDef<Set<TriTuple<NewA, NewB, NewC>>> collectPattern, ViewItem<?> accumulatePattern) {
        Variable<TriTuple<NewA, NewB, NewC>> newTuple = (Variable<TriTuple<NewA, NewB, NewC>>) createVariable(TriTuple.class,
                "groupKey", from(newSource));
        Variable<NewA> newA = createVariable("newA");
        Variable<NewB> newB = createVariable("newB");
        Variable<NewC> newC = createVariable("newC");
        DroolsPatternBuilder<TriTuple<NewA, NewB, NewC>> newPrimaryPattern = new DroolsPatternBuilder<>(newTuple)
                .expand(p -> p.bind(newA, tuple -> tuple.a))
                .expand(p -> p.bind(newB, tuple -> tuple.b))
                .expand(p -> p.bind(newC, tuple -> tuple.c));
        return new DroolsTriRuleStructure<>(newA, newB, newC, newPrimaryPattern, mergeShelved(accumulatePattern),
                singletonList(collectPattern), emptyList(), getVariableIdSupplier());
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadRuleStructure<NewA, NewB, NewC, NewD, QuadTuple<NewA, NewB, NewC, NewD>>
            regroupBiToQuad(Variable<Set<QuadTuple<NewA, NewB, NewC, NewD>>> newSource,
                    PatternDef<Set<QuadTuple<NewA, NewB, NewC, NewD>>> collectPattern, ViewItem<?> accumulatePattern) {
        Variable<QuadTuple<NewA, NewB, NewC, NewD>> newTuple = (Variable<QuadTuple<NewA, NewB, NewC, NewD>>) createVariable(
                QuadTuple.class, "groupKey", from(newSource));
        Variable<NewA> newA = createVariable("newA");
        Variable<NewB> newB = createVariable("newB");
        Variable<NewC> newC = createVariable("newC");
        Variable<NewD> newD = createVariable("newD");
        DroolsPatternBuilder<QuadTuple<NewA, NewB, NewC, NewD>> newPrimaryPattern = new DroolsPatternBuilder<>(newTuple)
                .expand(p -> p.bind(newA, tuple -> tuple.a))
                .expand(p -> p.bind(newB, tuple -> tuple.b))
                .expand(p -> p.bind(newC, tuple -> tuple.c))
                .expand(p -> p.bind(newD, tuple -> tuple.d));
        return new DroolsQuadRuleStructure<>(newA, newB, newC, newD, newPrimaryPattern, mergeShelved(accumulatePattern),
                singletonList(collectPattern), emptyList(), getVariableIdSupplier());
    }

    /**
     * Determines the types we expect to see as the result of this rule.
     *
     * <p>
     * Example 1:
     * For {@link DroolsBiRuleStructure} before regrouping, we expect this to return an array of A and B's fact type.
     *
     * <p>
     * Example 2:
     * For {@link DroolsBiRuleStructure} after regrouping, we expect this to return an array consisting of only
     * {@link BiTuple} class.
     *
     * @return never null
     */
    public Class[] getExpectedJustificationTypes() {
        PatternDef<PatternVar> pattern = getPrimaryPatternBuilder().build();
        Class<PatternVar> type = pattern.getFirstVariable().getType();
        if (FactTuple.class.isAssignableFrom(type)) {
            // There is one expected constraint justification, and that is of the tuple type.
            return new Class[] { type };
        }
        // There are plenty expected constraint justifications, one for each variable.
        return getVariableTypes();
    }

    abstract protected Class[] getVariableTypes();

}
