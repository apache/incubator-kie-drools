package org.optaplanner.core.impl.localsearch.decider.acceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge.GreatDelugeAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.hillclimbing.HillClimbingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance.LateAcceptanceAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.EntityTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.MoveTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.ValueTabuAcceptor;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

class AcceptorFactoryTest {

    @Test
    <Solution_> void buildCompositeAcceptor() {
        LocalSearchAcceptorConfig localSearchAcceptorConfig = new LocalSearchAcceptorConfig()
                .withAcceptorTypeList(Arrays.asList(AcceptorType.values()))
                .withEntityTabuSize(1)
                .withFadingEntityTabuSize(1)
                .withMoveTabuSize(1)
                .withFadingMoveTabuSize(1)
                .withUndoMoveTabuSize(1)
                .withValueTabuSize(1)
                .withFadingValueTabuSize(1)
                .withLateAcceptanceSize(10)
                .withSimulatedAnnealingStartingTemperature("-10hard/-10soft")
                .withStepCountingHillClimbingSize(1)
                .withStepCountingHillClimbingType(StepCountingHillClimbingType.IMPROVING_STEP);

        HeuristicConfigPolicy<Solution_> heuristicConfigPolicy = mock(HeuristicConfigPolicy.class);
        ScoreDefinition<HardSoftScore> scoreDefinition = new HardSoftScoreDefinition();
        when(heuristicConfigPolicy.getEnvironmentMode()).thenReturn(EnvironmentMode.NON_INTRUSIVE_FULL_ASSERT);
        when(heuristicConfigPolicy.getScoreDefinition()).thenReturn(scoreDefinition);

        AcceptorFactory<Solution_> acceptorFactory = AcceptorFactory.create(localSearchAcceptorConfig);
        Acceptor<Solution_> acceptor = acceptorFactory.buildAcceptor(heuristicConfigPolicy);
        assertThat(acceptor).isExactlyInstanceOf(CompositeAcceptor.class);
        CompositeAcceptor<Solution_> compositeAcceptor = (CompositeAcceptor<Solution_>) acceptor;
        assertThat(compositeAcceptor.acceptorList).hasSize(AcceptorType.values().length);
        assertAcceptorTypeAtPosition(compositeAcceptor, 0, HillClimbingAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 1, StepCountingHillClimbingAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 2, EntityTabuAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 3, ValueTabuAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 4, MoveTabuAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 5, MoveTabuAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 6, SimulatedAnnealingAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 7, LateAcceptanceAcceptor.class);
        assertAcceptorTypeAtPosition(compositeAcceptor, 8, GreatDelugeAcceptor.class);
    }

    private <Solution_, Acceptor_ extends Acceptor<Solution_>> void assertAcceptorTypeAtPosition(
            CompositeAcceptor<Solution_> compositeAcceptor, int position, Class<Acceptor_> expectedAcceptorType) {
        assertThat(compositeAcceptor.acceptorList.get(position)).isExactlyInstanceOf(expectedAcceptorType);
    }

    @Test
    <Solution_> void noAcceptorConfigured_throwsException() {
        AcceptorFactory<Solution_> acceptorFactory = AcceptorFactory.create(new LocalSearchAcceptorConfig());
        assertThatIllegalArgumentException().isThrownBy(() -> acceptorFactory.buildAcceptor(mock(HeuristicConfigPolicy.class)))
                .withMessageContaining("The acceptor does not specify any acceptorType");
    }
}
