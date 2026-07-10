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

package org.optaplanner.core.impl.testdata.domain.shadow.order;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PiggybackShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.DummyVariableListener;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataShadowVariableOrderEntity extends TestdataObject {

    public static EntityDescriptor<TestdataShadowVariableOrderSolution> buildEntityDescriptor() {
        return TestdataShadowVariableOrderSolution.buildSolutionDescriptor()
                .findEntityDescriptorOrFail(TestdataShadowVariableOrderEntity.class);
    }

    /*
     * The variables correspond to the scenario described in shadowVariableOrder.png. The last letter matches the variable name
     * on the diagram. 'xN' is an artificial prefix to force an order in which the fields are iterated that is different from
     * the alphabetical order of the original variable names (which, coincidentally, is the expected order of variable
     * listeners).
     */

    /**
     * G -> F
     */
    @PiggybackShadowVariable(shadowVariableName = "x4F")
    private String x0G;

    /**
     * D -> C
     */
    @ShadowVariable(variableListenerClass = DVariableListener.class, sourceVariableName = "x3C")
    private String x1D;

    /**
     * E -> {B, C}
     */
    @ShadowVariable(variableListenerClass = EVariableListener.class, sourceVariableName = "x5B")
    @ShadowVariable(variableListenerClass = EVariableListener.class, sourceVariableName = "x3C")
    private String x2E;

    /**
     * C -> A
     */
    @ShadowVariable(variableListenerClass = CVariableListener.class, sourceVariableName = "x6A")
    private String x3C;

    /**
     * F -> E
     */
    @ShadowVariable(variableListenerClass = FGVariableListener.class, sourceVariableName = "x2E")
    private String x4F;

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue x5B;
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    private TestdataValue x6A;

    public TestdataShadowVariableOrderEntity() {
    }

    public TestdataShadowVariableOrderEntity(String code) {
        super(code);
    }

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    abstract static class VariableListenerWithToString
            extends DummyVariableListener<TestdataShadowVariableOrderSolution, TestdataShadowVariableOrderEntity> {

        @Override
        public String toString() {
            return getClass().getSimpleName().replace("VariableListener", "");
        }
    }

    public static class CVariableListener extends VariableListenerWithToString {
    }

    public static class DVariableListener extends VariableListenerWithToString {
    }

    public static class EVariableListener extends VariableListenerWithToString {
    }

    public static class FGVariableListener extends VariableListenerWithToString {
    }
}
