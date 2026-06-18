/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.domain.entity.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.extended.entity.TestdataExtendedEntitySolution;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedSolution;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataExtendedPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.extended.TestdataExtendedPinnedSolution;

class EntityDescriptorTest {

    @Test
    void movableEntitySelectionFilter() {
        ScoreDirector<TestdataPinnedSolution> scoreDirector = mock(ScoreDirector.class);
        EntityDescriptor<TestdataPinnedSolution> entityDescriptor = TestdataPinnedEntity.buildEntityDescriptor();
        assertThat(entityDescriptor.hasEffectiveMovableEntitySelectionFilter()).isTrue();
        SelectionFilter<TestdataPinnedSolution, Object> movableEntitySelectionFilter =
                entityDescriptor.getEffectiveMovableEntitySelectionFilter();
        assertThat(movableEntitySelectionFilter).isNotNull();

        assertThat(movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e1", null, false, false))).isTrue();
        assertThat(movableEntitySelectionFilter.accept(scoreDirector,
                new TestdataPinnedEntity("e2", null, true, false))).isFalse();
    }

    @Test
    @Disabled // TODO FIXME PLANNER-849
    void extendedMovableEntitySelectionFilterUsedByParentSelector() {
        ScoreDirector<TestdataExtendedPinnedSolution> scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor<TestdataExtendedPinnedSolution> solutionDescriptor =
                TestdataExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor<TestdataExtendedPinnedSolution> parentEntityDescriptor =
                solutionDescriptor.findEntityDescriptor(TestdataPinnedEntity.class);
        assertThat(parentEntityDescriptor.hasEffectiveMovableEntitySelectionFilter()).isTrue();
        SelectionFilter<TestdataExtendedPinnedSolution, Object> parentMovableEntitySelectionFilter =
                parentEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
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
    void extendedMovableEntitySelectionFilterUsedByChildSelector() {
        ScoreDirector<TestdataExtendedPinnedSolution> scoreDirector = mock(ScoreDirector.class);
        SolutionDescriptor<TestdataExtendedPinnedSolution> solutionDescriptor =
                TestdataExtendedPinnedSolution.buildSolutionDescriptor();

        EntityDescriptor<TestdataExtendedPinnedSolution> childEntityDescriptor =
                solutionDescriptor.findEntityDescriptor(TestdataExtendedPinnedEntity.class);
        assertThat(childEntityDescriptor.hasEffectiveMovableEntitySelectionFilter()).isTrue();
        SelectionFilter<TestdataExtendedPinnedSolution, Object> childMovableEntitySelectionFilter =
                childEntityDescriptor.getEffectiveMovableEntitySelectionFilter();
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

    @Test
    void extractExtendedEntities() {
        TestdataExtendedEntitySolution solution = new TestdataExtendedEntitySolution();

        TestdataEntity entity = new TestdataEntity("entity-singleton");
        solution.setEntity(entity);

        TestdataUnannotatedExtendedEntity subEntity = new TestdataUnannotatedExtendedEntity("subEntity-singleton");
        solution.setSubEntity(subEntity);

        TestdataEntity e1 = new TestdataEntity("entity1");
        TestdataEntity e2 = new TestdataEntity("entity2");
        solution.setEntityList(List.of(e1, e2));

        TestdataUnannotatedExtendedEntity s1 = new TestdataUnannotatedExtendedEntity("subEntity1");
        TestdataUnannotatedExtendedEntity s2 = new TestdataUnannotatedExtendedEntity("subEntity2");
        TestdataUnannotatedExtendedEntity s3 = new TestdataUnannotatedExtendedEntity("subEntity3");
        solution.setSubEntityList(List.of(s1, s2, s3));

        TestdataUnannotatedExtendedEntity r1 = new TestdataUnannotatedExtendedEntity("subEntity1-R");
        TestdataUnannotatedExtendedEntity r2 = new TestdataUnannotatedExtendedEntity("subEntity2-R");
        solution.setRawEntityList(List.of(r1, r2));

        TestdataEntity e3 = new TestdataEntity("entity3");
        TestdataEntity e4 = new TestdataEntity("entity4");
        String randomData = "randomData";
        solution.setObjectEntityList(List.of(e3, e4, randomData));

        EntityDescriptor<TestdataExtendedEntitySolution> entityDescriptor =
                TestdataExtendedEntitySolution.buildEntityDescriptor();
        assertThat(entityDescriptor.extractEntities(solution))
                .containsExactlyInAnyOrder(entity, subEntity, e1, e2, e3, e4, s1, s2, s3, r1, r2);
    }
}
