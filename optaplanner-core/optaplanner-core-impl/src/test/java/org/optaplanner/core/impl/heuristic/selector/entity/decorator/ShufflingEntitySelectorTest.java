package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

class ShufflingEntitySelectorTest {

    @Test
    void isNeverEnding() {
        ShufflingEntitySelector selector = new ShufflingEntitySelector(mock(EntitySelector.class), SelectionCacheType.PHASE);
        assertThat(selector.isNeverEnding()).isFalse();
    }

    @Test
    void isCountable() {
        ShufflingEntitySelector selector = new ShufflingEntitySelector(mock(EntitySelector.class), SelectionCacheType.PHASE);
        assertThat(selector.isCountable()).isTrue();
    }

}
