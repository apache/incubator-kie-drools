/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;

public class ShufflingEntitySelectorTest {

    @Test
    public void isNeverEnding() {
        ShufflingEntitySelector selector = new ShufflingEntitySelector(mock(EntitySelector.class), SelectionCacheType.PHASE);
        assertThat(selector.isNeverEnding()).isFalse();
    }

    @Test
    public void isCountable() {
        ShufflingEntitySelector selector = new ShufflingEntitySelector(mock(EntitySelector.class), SelectionCacheType.PHASE);
        assertThat(selector.isCountable()).isTrue();
    }

}
