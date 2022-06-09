package org.optaplanner.quarkus.bean;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;

import io.quarkus.arc.DefaultBean;

/**
 * Throws an exception if an application tries to inject beans and the OptaPlanner Quarkus extension is skipped
 * due to missing domain classes.
 */
public class UnavailableOptaPlannerBeanProvider {

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> SolverFactory<Solution_> solverFactory() {
        throw createException(SolverFactory.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager() {
        throw createException(SolverManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleScore> scoreManager_workaroundSimpleScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleLongScore> scoreManager_workaroundSimpleLongScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, SimpleBigDecimalScore> scoreManager_workaroundSimpleBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftScore> scoreManager_workaroundHardSoftScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftLongScore> scoreManager_workaroundHardSoftLongScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardSoftBigDecimalScore> scoreManager_workaroundHardSoftBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftScore> scoreManager_workaroundHardMediumSoftScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftLongScore> scoreManager_workaroundHardMediumSoftLongScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, HardMediumSoftBigDecimalScore>
            scoreManager_workaroundHardMediumSoftBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, BendableScore> scoreManager_workaroundBendableScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, BendableLongScore> scoreManager_workaroundBendableLongScore() {
        throw createException(ScoreManager.class);
    }

    @DefaultBean
    @Singleton
    @Produces
    <Solution_> ScoreManager<Solution_, BendableBigDecimalScore> scoreManager_workaroundBendableBigDecimalScore() {
        throw createException(ScoreManager.class);
    }

    private RuntimeException createException(Class<?> beanClass) {
        return new IllegalStateException("The " + beanClass.getName() + " is not available as there are no @"
                + PlanningSolution.class.getSimpleName() + " or @" + PlanningEntity.class.getSimpleName()
                + " annotated classes."
                + "\nIf your domain classes are located in a dependency of this project, maybe try generating"
                + " the Jandex index by using the jandex-maven-plugin in that dependency, or by adding"
                + "application.properties entries (quarkus.index-dependency.<name>.group-id"
                + " and quarkus.index-dependency.<name>.artifact-id).");
    }
}
