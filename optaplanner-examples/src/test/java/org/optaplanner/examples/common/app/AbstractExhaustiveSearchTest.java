package org.optaplanner.examples.common.app;

import java.util.Arrays;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractExhaustiveSearchTest<Solution_>
        extends AbstractPhaseTest<Solution_, ExhaustiveSearchType> {

    @Override
    protected Stream<ExhaustiveSearchType> solverFactoryParams() {
        return Stream.of(ExhaustiveSearchType.values());
    }

    @Override
    protected SolverFactory<Solution_> buildSolverFactory(
            CommonApp<Solution_> commonApp,
            ExhaustiveSearchType exhaustiveSearchType) {
        String solverConfigResource = commonApp.getSolverConfigResource();
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        solverConfig.setTerminationConfig(new TerminationConfig());
        ExhaustiveSearchPhaseConfig exhaustiveSearchPhaseConfig = new ExhaustiveSearchPhaseConfig();
        exhaustiveSearchPhaseConfig.setExhaustiveSearchType(exhaustiveSearchType);
        solverConfig.setPhaseConfigList(Arrays.asList(exhaustiveSearchPhaseConfig));
        return SolverFactory.create(solverConfig);
    }
}
