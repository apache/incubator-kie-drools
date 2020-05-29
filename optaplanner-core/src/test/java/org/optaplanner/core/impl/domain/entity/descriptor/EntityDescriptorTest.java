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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataLegacyPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataExtendedPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataExtendedPinnedSolution;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataLegacyExtendedPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataLegacyExtendedPinnedSolution;

public class EntityDescriptorTest {

    @Test
    public void legacyMovableEntitySelectionFilter() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataLegacyPinnedEntity.buildEntityDescriptor();
        assertEquals(true, entityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter movableEntitySelectionFilter = entityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertNotNull(movableEntitySelectionFilter);

        assertEquals(true, movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyPinnedEntity("e1", null, false, false)));
        assertEquals(false, movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyPinnedEntity("e2", null, true, false)));
    }

    @Test
    public void movableEntitySelectionFilter() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor entityDescriptor = TestdataPinnedEntity.buildEntityDescriptor();
        assertEquals(true, entityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter movableEntitySelectionFilter = entityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertNotNull(movableEntitySelectionFilter);

        assertEquals(true, movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e1", null, false, false)));
        assertEquals(false, movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e2", null, true, false)));
    }

    @Test
    @Disabled // TODO FIXME PLANNER-849
    public void extendedMovableEntitySelectionFilterUsedByParentSelector() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor parentEntityDescriptor = solutionDescriptor.findEntityDescriptor(TestdataPinnedEntity.class);
        assertEquals(true, parentEntityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter parentMovableEntitySelectionFilter = parentEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertNotNull(parentMovableEntitySelectionFilter);

        assertEquals(true, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e1", null, false, false)));
        assertEquals(false, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e2", null, true, false)));
        assertEquals(true, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e3", null, false, false, null, false, false)));
        assertEquals(false, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e4", null, true, false, null, false, false)));
        assertEquals(false, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e5", null, false, true, null, false, false)));
        assertEquals(false, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e6", null, false, false, null, true, false)));
        assertEquals(false, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e7", null, false, false, null, false, true)));
        assertEquals(false, parentMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e8", null, true, true, null, true, true)));
    }

    @Test
    public void legacyExtendedMovableEntitySelectionFilterUsedByChildSelector() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataLegacyExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor childEntityDescriptor =
                solutionDescriptor.findEntityDescriptor(TestdataLegacyExtendedPinnedEntity.class);
        assertEquals(true, childEntityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter childMovableEntitySelectionFilter = childEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertNotNull(childMovableEntitySelectionFilter);

        // No new TestdataLegacyPinnedEntity() because a child selector would never select a pure parent instance
        assertEquals(true, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyExtendedPinnedEntity("e3", null, false, false, null, false, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyExtendedPinnedEntity("e4", null, true, false, null, false, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyExtendedPinnedEntity("e5", null, false, true, null, false, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyExtendedPinnedEntity("e6", null, false, false, null, true, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyExtendedPinnedEntity("e7", null, false, false, null, false, true)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataLegacyExtendedPinnedEntity("e8", null, true, true, null, true, true)));
    }

    @Test
    public void extendedMovableEntitySelectionFilterUsedByChildSelector() {
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor childEntityDescriptor = solutionDescriptor.findEntityDescriptor(TestdataExtendedPinnedEntity.class);
        assertEquals(true, childEntityDescriptor.hasEffectiveMovableEntitySelectionFilter());
        SelectionFilter childMovableEntitySelectionFilter = childEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertNotNull(childMovableEntitySelectionFilter);

        // No new TestdataPinnedEntity() because a child selector would never select a pure parent instance
        assertEquals(true, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e3", null, false, false, null, false, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e4", null, true, false, null, false, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e5", null, false, true, null, false, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e6", null, false, false, null, true, false)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e7", null, false, false, null, false, true)));
        assertEquals(false, childMovableEntitySelectionFilter.accept(scoreDirector,
                new TestdataExtendedPinnedEntity("e8", null, true, true, null, true, true)));
    }

}
