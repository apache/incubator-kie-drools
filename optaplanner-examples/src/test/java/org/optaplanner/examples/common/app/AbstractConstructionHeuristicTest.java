package org.optaplanner.examples.common.app;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractConstructionHeuristicTest<Solution_>
        extends AbstractPhaseTest<Solution_, ConstructionHeuristicType> {

    protected Predicate<ConstructionHeuristicType> includeConstructionHeuristicType() {
        return constructionHeuristicType -> true;
    }

    @Override
    protected Stream<ConstructionHeuristicType> solverFactoryParams() {
        return Stream.of(ConstructionHeuristicType.values()).filter(includeConstructionHeuristicType());
    }

    @Override
    protected SolverFactory<Solution_> buildSolverFactory(CommonApp<Solution_> commonApp,
            ConstructionHeuristicType constructionHeuristicType) {
        String solverConfigResource = commonApp.getSolverConfigResource();
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        solverConfig.setTerminationConfig(new TerminationConfig());
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(constructionHeuristicType);
        solverConfig.setPhaseConfigList(Arrays.asList(constructionHeuristicPhaseConfig));
        return SolverFactory.create(solverConfig);
    }
}
