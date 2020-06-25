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

package org.optaplanner.core.impl.domain.entity.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataExtendedPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataExtendedPinnedSolution;

public class EntityDescriptorTest {

    @Test
    public void movableEntitySelectionFilter() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataPinnedEntity.buildEntityDescriptor();
        assertThat(entityDescriptor.hasEffectiveMovableEntitySelectionFilter()).isTrue();
        SelectionFilter movableEntitySelectionFilter = entityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertThat(movableEntitySelectionFilter).isNotNull();

        assertThat(movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e1", null, false, false))).isTrue();
        assertThat(movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e2", null, true, false))).isFalse();
    }

    @Test
    @Disabled // TODO FIXME PLANNER-849
    public void extendedMovableEntitySelectionFilterUsedByParentSelector() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor parentEntityDescriptor = solutionDescriptor.findEntityDescriptor(TestdataPinnedEntity.class);
        assertThat(parentEntityDescriptor.hasEffectiveMovableEntitySelectionFilter()).isTrue();
        SelectionFilter parentMovableEntitySelectionFilter = parentEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertThat(parentMovableEntitySelectionFilter).isNotNull();

        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e1", null, false, false))).isTrue();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e2", null, true, false))).isFalse();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e3", null, false, false, null, false, false))).isTrue();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e4", null, true, false, null, false, false))).isFalse();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e5", null, false, true, null, false, false))).isFalse();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e6", null, false, false, null, true, false))).isFalse();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e7", null, false, false, null, false, true))).isFalse();
        assertThat(parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e8", null, true, true, null, true, true))).isFalse();
    }

    @Test
    public void extendedMovableEntitySelectionFilterUsedByChildSelector() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor childEntityDescriptor = solutionDescriptor.findEntityDescriptor(TestdataExtendedPinnedEntity.class);
        assertThat(childEntityDescriptor.hasEffectiveMovableEntitySelectionFilter()).isTrue();
        SelectionFilter childMovableEntitySelectionFilter = childEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertThat(childMovableEntitySelectionFilter).isNotNull();

        // No new TestdataPinnedEntity() because a child selector would never select a pure parent instance
        assertThat(childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e3", null, false, false, null, false, false))).isTrue();
        assertThat(childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e4", null, true, false, null, false, false))).isFalse();
        assertThat(childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e5", null, false, true, null, false, false))).isFalse();
        assertThat(childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e6", null, false, false, null, true, false))).isFalse();
        assertThat(childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e7", null, false, false, null, false, true))).isFalse();
        assertThat(childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e8", null, true, true, null, true, true))).isFalse();
    }

}
