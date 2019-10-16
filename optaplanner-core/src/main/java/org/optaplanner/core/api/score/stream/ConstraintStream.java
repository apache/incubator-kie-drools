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

package org.optaplanner.core.api.score.stream;

import java.util.stream.Stream;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * A constraint stream is a declaration on how to match {@link UniConstraintStream one}, {@link BiConstraintStream two}
 * or more objects.
 * Constraint steams are similar to a declaration of a JDK {@link Stream} or an SQL query,
 * but they support incremental score calculation
 * and {@link ScoreDirector#getConstraintMatchTotalMap()} score justification}.
 * <p>
 * An object that passes through constraint streams is called a fact.
 * It's either a {@link ProblemFactCollectionProperty problem fact} or a {@link PlanningEntity planning entity}.
 * <p>
 * A constraint stream is typically created with {@link ConstraintFactory#from(Class)}
 * or {@link UniConstraintStream#join(UniConstraintStream, BiJoiner)} by joining another constraint stream}.
 * Constraint streams form a directed, non-cyclic graph, with multiple start nodes (which listen to fact changes)
 * and one end node per {@link Constraint} (which affect the {@link Score}).
 * <p>
 * Throughout this documentation, we will be using the following terminology:
 *
 * <dl>
 *     <dt>Constraint Stream</dt>
 *          <dd>A chain of different operations, originated by {@link ConstraintFactory#from(Class)} (or similar
 *          methods) and terminated by a penalization or reward operation.</dd>
 *     <dt>Operation</dt>
 *          <dd>Operations (implementations of {@link ConstraintStream}) are parts of a constraint stream which mutate
 *          it.
 *          They may remove tuples from further evaluation, expand or contract streams. Every constraint stream has
 *          a terminal operation, which is either a penalization or a reward.</dd>
 *     <dt>Fact</dt>
 *          <dd>Object instance entering the constraint stream.</dd>
 *     <dt>Genuine Fact</dt>
 *          <dd>Fact that enters the constraint stream either through a from(...) call or through a join(...) call.
 *          Genuine facts are either planning entities (see {@link PlanningEntity}) or problem facts (see
 *          {@link ProblemFactProperty} or {@link ProblemFactCollectionProperty}).</dd>
 *     <dt>Inferred Fact</dt>
 *          <dd>Fact that enters the constraint stream through a computation.
 *          This would typically happen through an operation such as groupBy(...).</dd>
 *     <dt>Tuple</dt>
 *          <dd>A collection of facts that the constraint stream operates on, propagating them from operation to
 *          operation.
 *          For example, {@link UniConstraintStream} operates on single-fact tuples {A} and {@link BiConstraintStream}
 *          operates on two-fact tuples {A, B}.
 *          Putting facts into a tuple implies a relationship exists between these facts.</dd>
 *     <dt>Match</dt>
 *          <dd>Match is a tuple that reached the terminal operation of a constraint stream and is therefore either
 *          penalized or rewarded.</dd>
 *     <dt>Cardinality</dt>
 *          <dd>The number of facts in a tuple. Uni constraint streams have a cardinality of 1, bi constraint streams
 *          have a cardinality of 2, etc.</dd>
 *     <dt>Conversion</dt>
 *          <dd>An operation that changes the cardinality of a constraint stream.
 *          This typically happens through join(...) or a groupBy(...) operations.</dd>
 * </dl>
 */
public interface ConstraintStream {

    /**
     * The {@link ConstraintFactory} that build this.
     * @return never null
     */
    ConstraintFactory getConstraintFactory();

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    /**
     * Negatively impact the {@link Score}: subtract the constraintWeight for each match.
     * <p>
     * To avoid hard-coding the constraintWeight, to allow end-users to weak it,
     * use {@link #penalizeConfigurable(String)} and a {@link ConstraintConfiguration} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to the package of the {@link PlanningSolution} class.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @return never null
     */
    default Constraint penalize(String constraintName, Score<?> constraintWeight) {
        return penalize(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight);
    }

    /**
     * As defined by {@link #penalize(String, Score)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @return never null
     */
    Constraint penalize(String constraintPackage, String constraintName, Score<?> constraintWeight);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} for each match.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint is deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #penalize(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @return never null
     */
    default Constraint penalizeConfigurable(String constraintName) {
        return penalizeConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName);
    }

    /**
     * As defined by {@link #penalizeConfigurable(String)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @return never null
     */
    Constraint penalizeConfigurable(String constraintPackage, String constraintName);

    /**
     * Positively impact the {@link Score}: add the constraintWeight for each match.
     * <p>
     * To avoid hard-coding the constraintWeight, to allow end-users to weak it,
     * use {@link #penalizeConfigurable(String)} and a {@link ConstraintConfiguration} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to the package of the {@link PlanningSolution} class.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @param constraintWeight never null
     * @return never null
     */
    default Constraint reward(String constraintName, Score<?> constraintWeight) {
        return reward(getConstraintFactory().getDefaultConstraintPackage(), constraintName, constraintWeight);
    }

    /**
     * As defined by {@link #reward(String, Score)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @param constraintWeight never null
     * @return never null
     */
    Constraint reward(String constraintPackage, String constraintName, Score<?> constraintWeight);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} for each match.
     * <p>
     * The constraintWeight comes from an {@link ConstraintWeight} annotated member on the {@link ConstraintConfiguration},
     * so end users can change the constraint weights dynamically.
     * This constraint is deactivated if the {@link ConstraintWeight} is zero.
     * If there is no {@link ConstraintConfiguration}, use {@link #reward(String, Score)} instead.
     * <p>
     * The {@link Constraint#getConstraintPackage()} defaults to {@link ConstraintConfiguration#constraintPackage()}.
     * @param constraintName never null, shows up in {@link ConstraintMatchTotal} during score justification
     * @return never null
     */
    default Constraint rewardConfigurable(String constraintName) {
        return rewardConfigurable(getConstraintFactory().getDefaultConstraintPackage(), constraintName);
    }

    /**
     * As defined by {@link #rewardConfigurable(String)}.
     * @param constraintPackage never null
     * @param constraintName never null
     * @return never null
     */
    Constraint rewardConfigurable(String constraintPackage, String constraintName);

}
