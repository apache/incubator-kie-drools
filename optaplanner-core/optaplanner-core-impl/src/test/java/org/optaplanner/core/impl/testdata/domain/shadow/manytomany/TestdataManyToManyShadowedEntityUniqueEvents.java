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

package org.optaplanner.core.impl.testdata.domain.shadow.manytomany;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataManyToManyShadowedEntityUniqueEvents extends TestdataManyToManyShadowedEntity {

    public static EntityDescriptor<TestdataManyToManyShadowedSolution> buildEntityDescriptor() {
        return TestdataManyToManyShadowedSolution.buildSolutionDescriptorRequiresUniqueEvents()
                .findEntityDescriptorOrFail(TestdataManyToManyShadowedEntityUniqueEvents.class);
    }

    private final List<String> composedCodeLog = new ArrayList<>();

    public TestdataManyToManyShadowedEntityUniqueEvents(String code, TestdataValue primaryValue, TestdataValue secondaryValue) {
        super(code, primaryValue, secondaryValue);
    }

    @Override
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getPrimaryValue() {
        return super.getPrimaryValue();
    }

    @Override
    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getSecondaryValue() {
        return super.getSecondaryValue();
    }

    @Override
    @CustomShadowVariable(variableListenerClass = ComposedValuesUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "primaryValue"),
            @PlanningVariableReference(variableName = "secondaryValue") })
    public String getComposedCode() {
        return super.getComposedCode();
    }

    @Override
    public void setComposedCode(String composedCode) {
        // (2) log composedCode updates for later verification.
        composedCodeLog.add(composedCode);
        super.setComposedCode(composedCode);
    }

    public List<String> getComposedCodeLog() {
        return composedCodeLog;
    }

    @Override
    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "composedCode"))
    public String getReverseComposedCode() {
        return super.getReverseComposedCode();
    }

    public static class ComposedValuesUpdatingVariableListener
            extends TestdataManyToManyShadowedEntity.ComposedValuesUpdatingVariableListener {

        @Override
        public boolean requiresUniqueEntityEvents() {
            // (1) Override the original listener and require unique entity events.
            return true;
        }
    }
}
