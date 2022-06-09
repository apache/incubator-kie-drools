package org.optaplanner.core.config.localsearch.decider.forager;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.impl.localsearch.decider.forager.finalist.FinalistPodium;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.HighestScoreFinalistPodium;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.StrategicOscillationByLevelFinalistPodium;

@XmlEnum
public enum FinalistPodiumType {
    HIGHEST_SCORE,
    STRATEGIC_OSCILLATION,
    STRATEGIC_OSCILLATION_BY_LEVEL,
    STRATEGIC_OSCILLATION_BY_LEVEL_ON_BEST_SCORE;

    public <Solution_> FinalistPodium<Solution_> buildFinalistPodium() {
        switch (this) {
            case HIGHEST_SCORE:
                return new HighestScoreFinalistPodium<>();
            case STRATEGIC_OSCILLATION:
            case STRATEGIC_OSCILLATION_BY_LEVEL:
                return new StrategicOscillationByLevelFinalistPodium<>(false);
            case STRATEGIC_OSCILLATION_BY_LEVEL_ON_BEST_SCORE:
                return new StrategicOscillationByLevelFinalistPodium<>(true);
            default:
                throw new IllegalStateException("The finalistPodiumType (" + this + ") is not implemented.");
        }
    }

}
