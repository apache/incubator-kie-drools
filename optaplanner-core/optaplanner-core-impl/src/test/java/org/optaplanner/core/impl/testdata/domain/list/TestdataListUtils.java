/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.testdata.domain.list;

import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class TestdataListUtils {

    private TestdataListUtils() {
    }

    public static EntitySelector<TestdataListSolution> mockEntitySelector(Object... entities) {
        return SelectorTestUtils.mockEntitySelector(TestdataListEntity.class, entities);
    }

    public static EntityIndependentValueSelector<TestdataListSolution> mockEntityIndependentValueSelector(Object... values) {
        return SelectorTestUtils.mockEntityIndependentValueSelector(TestdataListEntity.class, "valueList", values);
    }

    public static ListVariableDescriptor<TestdataListSolution> getListVariableDescriptor(
            InnerScoreDirector<TestdataListSolution, ?> scoreDirector) {
        return (ListVariableDescriptor<TestdataListSolution>) scoreDirector
                .getSolutionDescriptor()
                .getEntityDescriptorStrict(TestdataListEntity.class)
                .getGenuineVariableDescriptor("valueList");
    }
}
