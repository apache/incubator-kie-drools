package org.optaplanner.core.impl.score.trend;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;

class InitializingScoreTrendTest {

    @Test
    void parseTrend() {
        assertThat(InitializingScoreTrend.parseTrend("ONLY_DOWN/ANY/ONLY_UP", 3).getTrendLevels())
                .isEqualTo(new InitializingScoreTrendLevel[] {
                        InitializingScoreTrendLevel.ONLY_DOWN,
                        InitializingScoreTrendLevel.ANY,
                        InitializingScoreTrendLevel.ONLY_UP });
    }

    @Test
    void isOnlyUp() {
        assertThat(InitializingScoreTrend.parseTrend("ONLY_UP/ONLY_UP/ONLY_UP", 3).isOnlyUp()).isTrue();
        assertThat(InitializingScoreTrend.parseTrend("ONLY_UP/ANY/ONLY_UP", 3).isOnlyUp()).isFalse();
        assertThat(InitializingScoreTrend.parseTrend("ONLY_UP/ONLY_UP/ONLY_DOWN", 3).isOnlyUp()).isFalse();
    }

    @Test
    void isOnlyDown() {
        assertThat(InitializingScoreTrend.parseTrend("ONLY_DOWN/ONLY_DOWN/ONLY_DOWN", 3).isOnlyDown()).isTrue();
        assertThat(InitializingScoreTrend.parseTrend("ONLY_DOWN/ANY/ONLY_DOWN", 3).isOnlyDown()).isFalse();
        assertThat(InitializingScoreTrend.parseTrend("ONLY_DOWN/ONLY_DOWN/ONLY_UP", 3).isOnlyDown()).isFalse();
    }

}
