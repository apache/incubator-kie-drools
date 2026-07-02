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

package org.optaplanner.core.impl.testdata.domain.shadow.wrong_listener;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.ListVariableListener;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

@PlanningEntity
public class TestdataWrongBasicShadowEntity {

    public static EntityDescriptor<TestdataSolution> buildEntityDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(
                TestdataSolution.class,
                TestdataEntity.class,
                TestdataWrongBasicShadowEntity.class).findEntityDescriptorOrFail(TestdataWrongBasicShadowEntity.class);
    }

    @ShadowVariable(variableListenerClass = MyListVariableListener.class,
            sourceEntityClass = TestdataEntity.class, sourceVariableName = "value")
    private String shadow;

    public String getShadow() {
        return shadow;
    }

    public void setShadow(String shadow) {
        this.shadow = shadow;
    }

    public static class MyListVariableListener implements ListVariableListener<TestdataSolution, TestdataEntity, Object> {

        @Override
        public void beforeEntityAdded(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity entity) {
        }

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity entity) {
        }

        @Override
        public void beforeEntityRemoved(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity entity) {
        }

        @Override
        public void afterEntityRemoved(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity entity) {
        }

        @Override
        public void afterListVariableElementUnassigned(ScoreDirector<TestdataSolution> scoreDirector, Object o) {
        }

        @Override
        public void beforeListVariableChanged(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity entity,
                int fromIndex, int toIndex) {
        }

        @Override
        public void afterListVariableChanged(ScoreDirector<TestdataSolution> scoreDirector, TestdataEntity entity,
                int fromIndex, int toIndex) {
        }
    }
}
