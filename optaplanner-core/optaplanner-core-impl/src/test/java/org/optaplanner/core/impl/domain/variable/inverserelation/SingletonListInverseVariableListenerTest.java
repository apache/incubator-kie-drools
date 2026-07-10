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

package org.optaplanner.core.impl.domain.variable.inverserelation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

class SingletonListInverseVariableListenerTest {

    private final ScoreDirector<TestdataListSolution> scoreDirector = mock(InnerScoreDirector.class);

    private final SingletonListInverseVariableListener<TestdataListSolution> inverseVariableListener =
            new SingletonListInverseVariableListener<>(
                    TestdataListValue.buildVariableDescriptorForEntity(),
                    TestdataListEntity.buildVariableDescriptorForValueList());

    @Test
    void inverseRelation() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListValue v4 = new TestdataListValue("4");
        TestdataListEntity e1 = new TestdataListEntity("a", v1, v2);
        TestdataListEntity e2 = new TestdataListEntity("b", v3);

        assertThat(v1.getEntity()).isNull();
        assertThat(v2.getEntity()).isNull();
        assertThat(v3.getEntity()).isNull();

        inverseVariableListener.beforeEntityAdded(scoreDirector, e1);
        inverseVariableListener.afterEntityAdded(scoreDirector, e1);
        inverseVariableListener.beforeEntityAdded(scoreDirector, e2);
        inverseVariableListener.afterEntityAdded(scoreDirector, e2);

        assertInverseEntity(v1, e1);
        assertInverseEntity(v2, e1);
        assertInverseEntity(v3, e2);

        // Move v1 from e1 to e2.
        inverseVariableListener.beforeListVariableChanged(scoreDirector, e1, 0, 1);
        e1.getValueList().remove(v1);
        inverseVariableListener.afterListVariableChanged(scoreDirector, e1, 0, 0);
        inverseVariableListener.beforeListVariableChanged(scoreDirector, e2, 1, 1);
        e2.getValueList().add(v1);
        inverseVariableListener.afterListVariableChanged(scoreDirector, e2, 1, 2);

        assertInverseEntity(v1, e2);

        // Assign v4 to e2[0].
        inverseVariableListener.beforeListVariableChanged(scoreDirector, e2, 0, 0);
        e2.getValueList().add(0, v4);
        inverseVariableListener.afterListVariableChanged(scoreDirector, e2, 0, 1);

        assertInverseEntity(v4, e2);

        // Unassign v2 from e1.
        inverseVariableListener.beforeListVariableChanged(scoreDirector, e1, 0, 1);
        e1.getValueList().remove(0);
        inverseVariableListener.afterListVariableElementUnassigned(scoreDirector, v2);
        inverseVariableListener.afterListVariableChanged(scoreDirector, e1, 0, 0);

        assertInverseEntity(v2, null);
    }

    @Test
    void removeEntity() {
        TestdataListValue v1 = new TestdataListValue("1");
        TestdataListValue v2 = new TestdataListValue("2");
        TestdataListValue v3 = new TestdataListValue("3");
        TestdataListEntity e1 = TestdataListEntity.createWithValues("a", v1, v2);
        TestdataListEntity e2 = TestdataListEntity.createWithValues("b", v3);

        assertThat(v1.getEntity()).isEqualTo(e1);
        assertThat(v2.getEntity()).isEqualTo(e1);
        assertThat(v3.getEntity()).isEqualTo(e2);

        inverseVariableListener.beforeEntityRemoved(scoreDirector, e1);
        inverseVariableListener.afterEntityRemoved(scoreDirector, e1);

        assertInverseEntity(v1, null);
        assertInverseEntity(v2, null);
        assertInverseEntity(v3, e2);
    }

    void assertInverseEntity(TestdataListValue element, Object entity) {
        assertThat(element.getEntity()).isEqualTo(entity);
        assertThat(inverseVariableListener.getInverseSingleton(element)).isEqualTo(entity);
    }
}
