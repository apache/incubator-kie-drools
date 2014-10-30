package org.optaplanner.core.impl.score.buildin;

import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

/**
 * for unit tests only!
 */
public class InitializingScoreTrendLevelFactory {

    public static InitializingScoreTrend createInitializingScoreTrendLevelArray(int arraySize, InitializingScoreTrendLevel trend) {
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[arraySize];
        for (int i = 0; i < arraySize; i++) {
            levels[i] = trend;
        }
        return new InitializingScoreTrend(levels);
    }
}
