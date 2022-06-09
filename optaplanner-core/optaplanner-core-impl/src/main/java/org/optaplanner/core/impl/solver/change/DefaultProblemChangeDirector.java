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

    public Score<?> doProblemChange(ProblemChange<Solution_> problemChange) {
        problemChange.doChange(scoreDirector.getWorkingSolution(), this);
        scoreDirector.triggerVariableListeners();
        return scoreDirector.calculateScore();
    }
}
