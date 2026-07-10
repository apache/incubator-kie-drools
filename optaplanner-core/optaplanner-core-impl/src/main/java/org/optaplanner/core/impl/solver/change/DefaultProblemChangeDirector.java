/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.solver.change;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public final class DefaultProblemChangeDirector<Solution_> implements ProblemChangeDirector {

    private final InnerScoreDirector<Solution_, ?> scoreDirector;

    public DefaultProblemChangeDirector(InnerScoreDirector<Solution_, ?> scoreDirector) {
        this.scoreDirector = scoreDirector;
    }

    @Override
    public <Entity> void addEntity(Entity entity, Consumer<Entity> entityConsumer) {
        Objects.requireNonNull(entity, () -> "Entity (" + entity + ") cannot be null.");
        Objects.requireNonNull(entityConsumer, () -> "Entity consumer (" + entityConsumer + ") cannot be null.");
        scoreDirector.beforeEntityAdded(entity);
        entityConsumer.accept(entity);
        scoreDirector.afterEntityAdded(entity);
    }

    @Override
    public <Entity> void removeEntity(Entity entity, Consumer<Entity> entityConsumer) {
        Objects.requireNonNull(entity, () -> "Entity (" + entity + ") cannot be null.");
        Objects.requireNonNull(entityConsumer, () -> "Entity consumer (" + entityConsumer + ") cannot be null.");
        Entity workingEntity = lookUpWorkingObjectOrFail(entity);
        scoreDirector.beforeEntityRemoved(workingEntity);
        entityConsumer.accept(workingEntity);
        scoreDirector.afterEntityRemoved(workingEntity);
    }

    @Override
    public <Entity> void changeVariable(Entity entity, String variableName, Consumer<Entity> entityConsumer) {
        Objects.requireNonNull(entity, () -> "Entity (" + entity + ") cannot be null.");
        Objects.requireNonNull(variableName, () -> "Planning variable name (" + variableName + ") cannot be null.");
        Objects.requireNonNull(entityConsumer, () -> "Entity consumer (" + entityConsumer + ") cannot be null.");
        Entity workingEntity = lookUpWorkingObjectOrFail(entity);
        scoreDirector.beforeVariableChanged(workingEntity, variableName);
        entityConsumer.accept(workingEntity);
        scoreDirector.afterVariableChanged(workingEntity, variableName);
    }

    @Override
    public <ProblemFact> void addProblemFact(ProblemFact problemFact, Consumer<ProblemFact> problemFactConsumer) {
        Objects.requireNonNull(problemFact, () -> "Problem fact (" + problemFact + ") cannot be null.");
        Objects.requireNonNull(problemFactConsumer, () -> "Problem fact consumer (" + problemFactConsumer
                + ") cannot be null.");
        scoreDirector.beforeProblemFactAdded(problemFact);
        problemFactConsumer.accept(problemFact);
        scoreDirector.afterProblemFactAdded(problemFact);
    }

    @Override
    public <ProblemFact> void removeProblemFact(ProblemFact problemFact, Consumer<ProblemFact> problemFactConsumer) {
        Objects.requireNonNull(problemFact, () -> "Problem fact (" + problemFact + ") cannot be null.");
        Objects.requireNonNull(problemFactConsumer, () -> "Problem fact consumer (" + problemFactConsumer
                + ") cannot be null.");
        ProblemFact workingProblemFact = lookUpWorkingObjectOrFail(problemFact);
        scoreDirector.beforeProblemFactRemoved(workingProblemFact);
        problemFactConsumer.accept(workingProblemFact);
        scoreDirector.afterProblemFactRemoved(workingProblemFact);
    }

    @Override
    public <EntityOrProblemFact> void changeProblemProperty(EntityOrProblemFact problemFactOrEntity,
            Consumer<EntityOrProblemFact> problemFactOrEntityConsumer) {
        Objects.requireNonNull(problemFactOrEntity,
                () -> "Problem fact or entity (" + problemFactOrEntity + ") cannot be null.");
        Objects.requireNonNull(problemFactOrEntityConsumer, () -> "Problem fact or entity consumer ("
                + problemFactOrEntityConsumer + ") cannot be null.");
        EntityOrProblemFact workingEntityOrProblemFact = lookUpWorkingObjectOrFail(problemFactOrEntity);
        scoreDirector.beforeProblemPropertyChanged(workingEntityOrProblemFact);
        problemFactOrEntityConsumer.accept(workingEntityOrProblemFact);
        scoreDirector.afterProblemPropertyChanged(workingEntityOrProblemFact);
    }

    @Override
    public <EntityOrProblemFact> EntityOrProblemFact lookUpWorkingObjectOrFail(EntityOrProblemFact externalObject) {
        return scoreDirector.lookUpWorkingObject(externalObject);
    }

    @Override
    public <EntityOrProblemFact> Optional<EntityOrProblemFact>
            lookUpWorkingObject(EntityOrProblemFact externalObject) {
        return Optional.ofNullable(scoreDirector.lookUpWorkingObjectOrReturnNull(externalObject));
    }

    @Override
    public void updateShadowVariables() {
        scoreDirector.triggerVariableListeners();
    }

    public Score<?> doProblemChange(ProblemChange<Solution_> problemChange) {
        problemChange.doChange(scoreDirector.getWorkingSolution(), this);
        updateShadowVariables();
        return scoreDirector.calculateScore();
    }
}
