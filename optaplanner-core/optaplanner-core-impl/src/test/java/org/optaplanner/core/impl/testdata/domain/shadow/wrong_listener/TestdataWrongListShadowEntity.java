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
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;

@PlanningEntity
public class TestdataWrongListShadowEntity {

    public static EntityDescriptor<TestdataListSolution> buildEntityDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(
                TestdataListSolution.class,
                TestdataListEntity.class,
                TestdataWrongListShadowEntity.class,
                TestdataListValue.class).findEntityDescriptorOrFail(TestdataWrongListShadowEntity.class);
    }

    @ShadowVariable(variableListenerClass = MyBasicVariableListener.class,
            sourceEntityClass = TestdataListEntity.class, sourceVariableName = "valueList")
    private String shadow;

    public String getShadow() {
        return shadow;
    }

    public void setShadow(String shadow) {
        this.shadow = shadow;
    }

    public static class MyBasicVariableListener implements VariableListener<TestdataListSolution, TestdataListEntity> {

        @Override
        public void beforeEntityAdded(ScoreDirector<TestdataListSolution> scoreDirector, TestdataListEntity entity) {
        }

        @Override
        public void afterEntityAdded(ScoreDirector<TestdataListSolution> scoreDirector, TestdataListEntity entity) {
        }

        @Override
        public void beforeEntityRemoved(ScoreDirector<TestdataListSolution> scoreDirector, TestdataListEntity entity) {
        }

        @Override
        public void afterEntityRemoved(ScoreDirector<TestdataListSolution> scoreDirector, TestdataListEntity entity) {
        }

        @Override
        public void beforeVariableChanged(ScoreDirector<TestdataListSolution> scoreDirector, TestdataListEntity entity) {
        }

        @Override
        public void afterVariableChanged(ScoreDirector<TestdataListSolution> scoreDirector, TestdataListEntity entity) {
        }
    }
}
