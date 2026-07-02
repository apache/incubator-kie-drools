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

package org.optaplanner.core.impl.testdata.domain.list.shadow_history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.IndexShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.NextElementShadowVariable;
import org.optaplanner.core.api.domain.variable.PreviousElementShadowVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;

@PlanningEntity
public class TestdataListValueWithShadowHistory extends TestdataObject {

    public static EntityDescriptor<TestdataListSolutionWithShadowHistory> buildEntityDescriptor() {
        return TestdataListSolutionWithShadowHistory.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataListValueWithShadowHistory.class);
    }

    private TestdataListEntityWithShadowHistory entity;
    private Integer index;
    private TestdataListValueWithShadowHistory previous;
    private TestdataListValueWithShadowHistory next;

    private final List<TestdataListEntityWithShadowHistory> entityHistory = new ArrayList<>();
    private final List<Integer> indexHistory = new ArrayList<>();
    private final List<TestdataListValueWithShadowHistory> previousHistory = new ArrayList<>();
    private final List<TestdataListValueWithShadowHistory> nextHistory = new ArrayList<>();

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

    @PreviousElementShadowVariable(sourceVariableName = "valueList")
    public TestdataListValueWithShadowHistory getPrevious() {
        return previous;
    }

    public void setPrevious(TestdataListValueWithShadowHistory previous) {
        this.previous = previous;
        previousHistory.add(previous);
    }

    @NextElementShadowVariable(sourceVariableName = "valueList")
    public TestdataListValueWithShadowHistory getNext() {
        return next;
    }

    public void setNext(TestdataListValueWithShadowHistory next) {
        this.next = next;
        nextHistory.add(next);
    }

    public List<TestdataListEntityWithShadowHistory> getEntityHistory() {
        return Collections.unmodifiableList(entityHistory);
    }

    public List<Integer> getIndexHistory() {
        return Collections.unmodifiableList(indexHistory);
    }

    public List<TestdataListValueWithShadowHistory> getPreviousHistory() {
        return Collections.unmodifiableList(previousHistory);
    }

    public List<TestdataListValueWithShadowHistory> getNextHistory() {
        return Collections.unmodifiableList(nextHistory);
    }

}
