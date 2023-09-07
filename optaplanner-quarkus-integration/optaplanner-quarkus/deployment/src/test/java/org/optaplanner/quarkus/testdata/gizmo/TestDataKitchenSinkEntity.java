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

package org.optaplanner.quarkus.testdata.gizmo;

import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PiggybackShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.domain.variable.ShadowVariable;

/*
 *  Should have one of every annotation, even annotations that
 *  don't make sense on an entity, to make sure everything works
 *  a-ok.
 */
@PlanningEntity
public class TestDataKitchenSinkEntity {

    private Integer intVariable;

    @CustomShadowVariable(
            variableListenerClass = DummyVariableListener.class,
            sources = {
                    @PlanningVariableReference(entityClass = TestDataKitchenSinkEntity.class,
                            variableName = "stringVariable")
            })
    private String shadow1;

    @ShadowVariable(
            variableListenerClass = DummyVariableListener.class,
            sourceEntityClass = TestDataKitchenSinkEntity.class, sourceVariableName = "stringVariable")
    private String shadow2;

    @PiggybackShadowVariable(shadowVariableName = "shadow2")
    private String piggybackShadow;

    @PlanningVariable(valueRangeProviderRefs = { "names" })
    private String stringVariable;

    private boolean isPinned;

    @PlanningVariable(valueRangeProviderRefs = { "ints" })
    private Integer getIntVariable() {
        return intVariable;
    }

    public void setIntVariable(Integer val) {
        intVariable = val;
    }

    public Integer testGetIntVariable() {
        return intVariable;
    }

    public String testGetStringVariable() {
        return stringVariable;
    }

    @PlanningPin
    private boolean isPinned() {
        return isPinned;
    }

    @ValueRangeProvider(id = "ints")
    private List<Integer> myIntValueRange() {
        return Collections.singletonList(1);
    }

    @ValueRangeProvider(id = "names")
    public List<String> myStringValueRange() {
        return Collections.singletonList("A");
    }

}
