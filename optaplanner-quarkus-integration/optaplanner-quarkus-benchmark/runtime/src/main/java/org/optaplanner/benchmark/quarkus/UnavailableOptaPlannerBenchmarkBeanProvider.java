package org.optaplanner.benchmark.quarkus;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;

import io.quarkus.arc.DefaultBean;

public class UnavailableOptaPlannerBenchmarkBeanProvider {

    @DefaultBean
    @Singleton
    @Produces
    PlannerBenchmarkFactory benchmarkFactory() {
        throw new IllegalStateException("The " + PlannerBenchmarkFactory.class.getName() + " is not available as there are no @"
                + PlanningSolution.class.getSimpleName() + " or @" + PlanningEntity.class.getSimpleName()
                + " annotated classes."
                + "\nIf your domain classes are located in a dependency of this project, maybe try generating"
                + " the Jandex index by using the jandex-maven-plugin in that dependency, or by adding"
                + "application.properties entries (quarkus.index-dependency.<name>.group-id"
                + " and quarkus.index-dependency.<name>.artifact-id).");
    }

}
