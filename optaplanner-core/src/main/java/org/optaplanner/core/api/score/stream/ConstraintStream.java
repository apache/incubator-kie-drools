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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * A constraint stream is a declaration on how to match {@link UniConstraintStream one}, {@link BiConstraintStream two}
 * or more objects.
 * Constraint steams are similar to a declaration of a JDK {@link Stream} or an SQL query,
 * but they support incremental score calculation
 * and {@link ScoreDirector#getConstraintMatchTotals() score justification}.
 * <p>
 * An object that passes through constraint streams is called a fact.
 * It's either a {@link ProblemFactCollectionProperty problem fact} or a {@link PlanningEntity planning entity}.
 * <p>
 * A constraint stream is typically created with {@link Constraint#from(Class)}
 * or {@link UniConstraintStream#join(UniConstraintStream, BiJoiner)} from another constraint stream}.
 * Constraint streams form a directed, non-cyclic graph, with multiple start nodes (which listen to fact changes)
 * and one end node per {@link Constraint} (which affect the {@link Score}).
 */
public interface ConstraintStream {

}
