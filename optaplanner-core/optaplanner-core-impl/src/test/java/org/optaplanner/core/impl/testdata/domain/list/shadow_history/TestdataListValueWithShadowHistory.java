/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.list.shadow_history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.IndexShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataListValueWithShadowHistory extends TestdataObject {

    public static EntityDescriptor<TestdataListSolutionWithShadowHistory> buildEntityDescriptor() {
        return TestdataListSolutionWithShadowHistory.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataListValueWithShadowHistory.class);
    }

    public static InverseRelationShadowVariableDescriptor<TestdataListSolutionWithShadowHistory>
            buildVariableDescriptorForEntity() {
        return (InverseRelationShadowVariableDescriptor<TestdataListSolutionWithShadowHistory>) buildEntityDescriptor()
                .getShadowVariableDescriptor("entity");
    }

    public static IndexShadowVariableDescriptor<TestdataListSolutionWithShadowHistory> buildVariableDescriptorForIndex() {
        return (IndexShadowVariableDescriptor<TestdataListSolutionWithShadowHistory>) buildEntityDescriptor()
                .getShadowVariableDescriptor("index");
    }

    private TestdataListEntityWithShadowHistory entity;
    private Integer index;

    private final List<TestdataListEntityWithShadowHistory> entityHistory = new ArrayList<>();
    private final List<Integer> indexHistory = new ArrayList<>();

    public TestdataListValueWithShadowHistory() {
    }

    public TestdataListValueWithShadowHistory(String code) {
        super(code);
    }

    @InverseRelationShadowVariable(sourceVariableName = "valueList")
    public TestdataListEntityWithShadowHistory getEntity() {
        return entity;
    }

    public void setEntity(TestdataListEntityWithShadowHistory entity) {
        this.entity = entity;
        entityHistory.add(entity);
    }

    @IndexShadowVariable(sourceVariableName = "valueList")
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
        indexHistory.add(index);
    }

    public List<TestdataListEntityWithShadowHistory> getEntityHistory() {
        return Collections.unmodifiableList(entityHistory);
    }

    public List<Integer> getIndexHistory() {
        return Collections.unmodifiableList(indexHistory);
    }

}
