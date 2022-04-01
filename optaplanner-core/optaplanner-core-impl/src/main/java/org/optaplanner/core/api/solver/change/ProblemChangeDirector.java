/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.solver.change;

import java.util.Optional;
import java.util.function.Consumer;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Allows external changes to the {@link PlanningSolution working solution}. If the changes are not applied through
 * the ProblemChangeDirector,
 * {@link org.optaplanner.core.api.domain.variable.VariableListener both internal and custom variable listeners} are
 * never notified about them, resulting to inconsistencies in the {@link PlanningSolution working solution}.
 * Should be used only from a {@link ProblemChange} implementation.
 *
 * To see an example implementation, please refer to the {@link ProblemChange} Javadoc.
 */
public interface ProblemChangeDirector {

    /**
     * Add a new {@link PlanningEntity} instance into the {@link PlanningSolution working solution}.
     *
     * @param entity never null; the {@link PlanningEntity} instance
     * @param entityConsumer never null; adds the entity to the {@link PlanningSolution working solution}
     * @param <Entity> the planning entity object type
     */
    <Entity> void addEntity(Entity entity, Consumer<Entity> entityConsumer);

    /**
     * Remove an existing {@link PlanningEntity} instance from the {@link PlanningSolution working solution}.
     * Translates the entity to a working planning entity by performing a lookup as defined by
     * {@link #lookUpWorkingObjectOrFail(Object)}.
     * 
     * @param entity never null; the {@link PlanningEntity} instance
     * @param entityConsumer never null; removes the working entity from the {@link PlanningSolution working solution}
     * @param <Entity> the planning entity object type
     */
    <Entity> void removeEntity(Entity entity, Consumer<Entity> entityConsumer);

    /**
     * Change a {@link PlanningVariable} value of a {@link PlanningEntity}. Translates the entity to a working
     * planning entity by performing a lookup as defined by {@link #lookUpWorkingObjectOrFail(Object)}.
     *
     * @param entity never null; the {@link PlanningEntity} instance
     * @param variableName never null; name of the {@link PlanningVariable}
     * @param entityConsumer never null; updates the value of the the {@link PlanningVariable} inside
     *        the {@link PlanningEntity}
     * @param <Entity> the planning entity object type
     */
    <Entity> void changeVariable(Entity entity, String variableName, Consumer<Entity> entityConsumer);

    /**
     * Add a new problem fact into the {@link PlanningSolution working solution}.
     *
     * @param problemFact never null; the problem fact instance
     * @param problemFactConsumer never null; removes the working problem fact from the
     *        {@link PlanningSolution working solution}
     * @param <ProblemFact> the problem fact object type
     */
    <ProblemFact> void addProblemFact(ProblemFact problemFact, Consumer<ProblemFact> problemFactConsumer);

    /**
     * Remove an existing problem fact from the {@link PlanningSolution working solution}. Translates the problem fact
     * to a working problem fact by performing a lookup as defined by {@link #lookUpWorkingObjectOrFail(Object)}.
     *
     * @param problemFact never null; the problem fact instance
     * @param problemFactConsumer never null; removes the working problem fact from the
     *        {@link PlanningSolution working solution}
     * @param <ProblemFact> the problem fact object type
     */
    <ProblemFact> void removeProblemFact(ProblemFact problemFact, Consumer<ProblemFact> problemFactConsumer);

    /**
     * Change a property of either a {@link PlanningEntity} or a problem fact. Translates the entity or the problem fact
     * to its {@link PlanningSolution working solution} counterpart by performing a lookup as defined by
     * {@link #lookUpWorkingObjectOrFail(Object)}.
     *
     * @param problemFactOrEntity never null; the {@link PlanningEntity} or the problem fact instance
     * @param problemFactOrEntityConsumer never null; updates the property of the {@link PlanningEntity}
     *        or the problem fact
     * @param <EntityOrProblemFact> the planning entity or problem fact object type
     */
    <EntityOrProblemFact> void changeProblemProperty(EntityOrProblemFact problemFactOrEntity,
            Consumer<EntityOrProblemFact> problemFactOrEntityConsumer);

    /**
     * Translate an entity or fact instance (often from another {@link Thread} or JVM)
     * to this {@link ProblemChangeDirector}'s internal working instance.
     * <p>
     * Matching is determined by the {@link LookUpStrategyType} on {@link PlanningSolution}.
     * Matching uses a {@link PlanningId} by default.
     *
     * @param externalObject sometimes null
     * @return null if externalObject is null
     * @throws IllegalArgumentException if there is no workingObject for externalObject, if it cannot be looked up
     *         or if the externalObject's class is not supported
     * @throws IllegalStateException if it cannot be looked up
     * @param <EntityOrProblemFact> the object type
     */
    <EntityOrProblemFact> EntityOrProblemFact lookUpWorkingObjectOrFail(EntityOrProblemFact externalObject);

    /**
     * As defined by {@link #lookUpWorkingObjectOrFail(Object)},
     * but doesn't fail fast if no workingObject was ever added for the externalObject.
     * It's recommended to use {@link #lookUpWorkingObjectOrFail(Object)} instead.
     *
     * @param externalObject sometimes null
     * @return {@link Optional#empty()} if externalObject is null or if there is no workingObject for externalObject
     * @throws IllegalArgumentException if it cannot be looked up or if the externalObject's class is not supported
     * @throws IllegalStateException if it cannot be looked up
     * @param <EntityOrProblemFact> the object type
     */
    <EntityOrProblemFact> Optional<EntityOrProblemFact> lookUpWorkingObject(EntityOrProblemFact externalObject);
}
