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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

import org.drools.model.DSL;
import org.drools.model.DeclarationSource;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

/**
 * Represents the left-hand side of a Drools rule.
 */
public abstract class DroolsRuleStructure {

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
     * amount of time looking up applicable instances of that variable, as it has to traverse instances of all types in
     * the working memory. Therefore, it is desirable to be as specific as possible.
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     * numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     * rule.
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public final <X> Variable<X> createVariable(Class<X> clz, String name) {
        return DSL.declarationOf(clz, decorateVariableName(name));
    }

    /**
     * Declare a new {@link Variable} in this rule, with a given name and a declaration source.
     * Delegates to {@link DSL#declarationOf(Class, String, DeclarationSource)}.
     * Creating variables via this method guarantees unique variable names within the context of a rule through the use
     * of {@link DroolsConstraintFactory#getVariableIdSupplier()}.
     *
     * @param clz type of the variable. Using {@link Object} will work in all cases, but Drools will spend unnecessary
     * amount of time looking up applicable instances of that variable, as it has to traverse instances of all types in
     * the working memory. Therefore, it is desirable to be as specific as possible.
     * @param name name of the variable, mostly useful for debugging purposes. Will be decorated by a pseudo-random
     * numeric identifier to prevent multiple variables of the same name to exist within left-hand side of a single
     * rule.
     * @param source declaration source of the variable
     * @param <X> Generic type of the variable.
     * @return new variable declaration, not yet bound to anything
     */
    public final <X> Variable<X> createVariable(Class<X> clz, String name, DeclarationSource source) {
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
     * Declares a new {@link Object}-typed variable, see {@link #createVariable(Class, String)} for
     * details.
     */
    public final <X> Variable<X> createVariable(String name, DeclarationSource source) {
        return (Variable<X>) createVariable(Object.class, name, source);
    }

    /**
     * Takes {@link #getSupportingRuleItems()}, puts them into a new {@link List}, and adds additional
     * {@link RuleItemBuilder}s after it.
     *
     * @param toAdd the additional items to add
     * @return new list containing both the existing supporting rule items and the new ones
     */
    public final List<RuleItemBuilder<?>> rebuildSupportingRuleItems(RuleItemBuilder<?>... toAdd) {
        List<RuleItemBuilder<?>> supporting = new ArrayList<>(getSupportingRuleItems());
        for (RuleItemBuilder<?> ruleItem : toAdd) {
            supporting.add(ruleItem);
        }
        return supporting;
    }

    public LongSupplier getVariableIdSupplier() {
        return variableIdSupplier;
    }

    /**
     * Returns the pattern that the subsequent streams may further expand. Consider the following left-hand side of a
     * Drools rule:
     *
     * <pre>
     *     $a1: A()
     *     $a2: A(this != $a1)
     * </pre>
     *
     * The primary pattern would be the latter one ($a2), as that is the pattern you would use to further expand your
     * output in both $a1 and $a2.
     *
     * @return the primary pattern as defined
     */
    public abstract DroolsPatternBuilder<Object> getPrimaryPattern();

    /**
     * Every other pattern necessary for the {@link #getPrimaryPattern()} to function properly within the Drools rule's
     * left-hand side. In the example rule (see {@link #getPrimaryPattern()}, this method would return one and only
     * supporting {@link RuleItemBuilder}, the one representing $a1.
     *
     * @return all supporting rule items as defined, in the correct order
     */
    public abstract List<RuleItemBuilder<?>> getSupportingRuleItems();

}
