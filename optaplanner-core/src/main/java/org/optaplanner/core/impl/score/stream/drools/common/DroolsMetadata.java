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

import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.model.Declaration;
import org.drools.model.PatternDSL.PatternDef;

import static org.drools.model.PatternDSL.pattern;

/**
 * Represents a variable on the left-hand side of a rule.
 * This variable can either be genuine or logical.
 *
 * <dl>
 *      <dt>Genuine variables look like this:</dt>
 *          <dd>$a: SomeFact()</dd>
 *          <dd>$b: SomeOtherFact()</dd>
 *      <dt>Logical variables look like this:</dt>
 *          <dd>LogicalTuple($a: getItem(0), $b: getItem(1))</dd>
 * </dl>
 *
 * Genuine variables become logical variables through an operation of logical insertion, which might look like this:
 *
 * <pre>
 *     rule "Rule with genuine facts"
 *     when
 *          $a: SomeFact()
 *          $b: SomeOtherFact()
 *     then
 *          insertLogical(new LogicalTuple($a, $b));
 *     end
 *
 *     rule "Rule with an inferred fact"
 *     when
 *          LogicalTuple($a: getItem(0), $b: getItem(1))
 *     then
 *          // some consequence
 *     end
 * </pre>
 *
 * This class is an abstraction, allowing code to read variables of both types.
 *
 * @param <LogicalFactType> The actual type of the fact in the current rule.
 * @param <GenuineFactType> The original type of the fact, in the original genuine rule.
 */
public interface DroolsMetadata<LogicalFactType, GenuineFactType> {

    static <A> DroolsInferredMetadata<A> ofInferred(Declaration<DroolsLogicalTuple> variableDeclaration,
            Supplier<PatternDef<DroolsLogicalTuple>> patternBuilder) {
        return ofInferred(variableDeclaration, patternBuilder, 0);
    }

    static <A> DroolsInferredMetadata<A> ofInferred(Declaration<DroolsLogicalTuple> variableDeclaration,
            Supplier<PatternDef<DroolsLogicalTuple>> patternBuilder, int itemId) {
        return new DroolsInferredMetadata<>(variableDeclaration, patternBuilder, itemId);
    }

    static <A> DroolsGenuineMetadata<A> ofGenuine(Declaration<A> variableDeclaration) {
        return new DroolsGenuineMetadata<>(variableDeclaration, () -> pattern(variableDeclaration));
    }

    /**
     * Extract variable value from the rule.
     * For genuine variables, this will be equivalent to {@link Function#identity()}.
     * For inferred variables, this will refer to the {@link DroolsLogicalTuple#getItem(int)}.
     * @param container the variable from Drools on which to operate
     * @return value of the variable
     */
    GenuineFactType extract(LogicalFactType container);

    Declaration<LogicalFactType> getVariableDeclaration();

    /**
     * A pattern is a way of accessing the variable in Drools.
     * In some rules, patterns may be reused for several different constructs.
     * Drools does not support reuse of pattern instances for different uses, and therefore we must create new ones
     * every time we want to use them.
     *
     * @return pattern to give access to the variable
     */
    PatternDef<LogicalFactType> buildPattern();

    /**
     * Create new {@link DroolsMetadata}, where its pattern will be different.
     *
     * @param patternBuilder the new pattern to be returned by {@link #buildPattern()}. Typically reuses the parent
     * pattern with some modifications.
     * @return New instance of {@link DroolsMetadata}.
     */
    DroolsMetadata<LogicalFactType, GenuineFactType> substitute(Supplier<PatternDef<LogicalFactType>> patternBuilder);

}
