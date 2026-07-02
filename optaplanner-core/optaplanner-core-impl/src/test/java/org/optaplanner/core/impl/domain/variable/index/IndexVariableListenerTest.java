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

package org.optaplanner.core.impl.domain.variable.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class IndexVariableListenerTest {

    private final ScoreDirector<TestdataListSolution> scoreDirector = mock(InnerScoreDirector.class);

    private final IndexVariableListener<TestdataListSolution> indexVariableListener = new IndexVariableListener<>(
            TestdataListValue.buildVariableDescriptorForIndex(),
            TestdataListEntity.buildVariableDescriptorForValueList());

    @Test
    void index() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity entity = new TestdataListEntity("a", v1, v2, v3);

        assertIndex(v1, null);
        assertIndex(v2, null);
        assertIndex(v3, null);
        assertIndex(v4, null);

        indexVariableListener.beforeEntityAdded(scoreDirector, entity);
        indexVariableListener.afterEntityAdded(scoreDirector, entity);

        assertIndex(v1, 0);
        assertIndex(v2, 1);
        assertIndex(v3, 2);

        // Assign v4.
        indexVariableListener.beforeListVariableChanged(scoreDirector, entity, 2, 2);
        entity.getValueList().add(2, v4);
        indexVariableListener.afterListVariableChanged(scoreDirector, entity, 2, 3);

        assertIndex(v1, 0);
        assertIndex(v2, 1);
        assertIndex(v4, 2);
        assertIndex(v3, 3);

        // Unassign v1.
        indexVariableListener.beforeListVariableChanged(scoreDirector, entity, 0, 1);
        entity.getValueList().remove(v1);
        indexVariableListener.afterListVariableElementUnassigned(scoreDirector, v1);
        indexVariableListener.afterListVariableChanged(scoreDirector, entity, 0, 0);

        assertIndex(v1, null);
        assertIndex(v2, 0);
        assertIndex(v4, 1);
        assertIndex(v3, 2);

        // Move v4 from entity[1] to entity[2].
        indexVariableListener.beforeListVariableChanged(scoreDirector, entity, 1, 3);
        entity.getValueList().remove(v4);
        entity.getValueList().add(2, v4);
        indexVariableListener.afterListVariableChanged(scoreDirector, entity, 1, 3);

        assertIndex(v2, 0);
        assertIndex(v3, 1);
        assertIndex(v4, 2);
    }

    @Test
    void removeEntity() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity entity = TestdataListEntity.createWithValues("a", v1, v2, v3);

        assertIndex(v1, 0);
        assertIndex(v2, 1);
        assertIndex(v3, 2);

        indexVariableListener.beforeEntityRemoved(scoreDirector, entity);
        indexVariableListener.afterEntityRemoved(scoreDirector, entity);

        assertIndex(v1, null);
        assertIndex(v2, null);
        assertIndex(v3, null);
    }

    void assertIndex(TestdataListValue element, Integer index) {
        assertThat(element.getIndex()).isEqualTo(index);
        assertThat(indexVariableListener.getIndex(element)).isEqualTo(index);
    }
}
