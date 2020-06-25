/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.shadow.cyclic.invalid;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerAdapter;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class TestdataCyclicReferencedShadowedEntity extends TestdataObject {

    public static EntityDescriptor buildEntityDescriptor() {
        SolutionDescriptor solutionDescriptor = TestdataCyclicReferencedShadowedSolution.buildSolutionDescriptor();
        return solutionDescriptor.findEntityDescriptorOrFail(TestdataCyclicReferencedShadowedEntity.class);
    }

    public static GenuineVariableDescriptor buildVariableDescriptorForValue() {
        SolutionDescriptor solutionDescriptor = TestdataCyclicReferencedShadowedSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor
                .findEntityDescriptorOrFail(TestdataCyclicReferencedShadowedEntity.class);
        return entityDescriptor.getGenuineVariableDescriptor("value");
    }

    private TestdataValue value;
    private boolean barber;
    private boolean cutsOwnHair;

    public TestdataCyclicReferencedShadowedEntity() {
    }

    public TestdataCyclicReferencedShadowedEntity(String code) {
        super(code);
    }

    public TestdataCyclicReferencedShadowedEntity(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    @PlanningVariable(valueRangeProviderRefs = "valueRange")
    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    @CustomShadowVariable(variableListenerClass = BarberAndCutsOwnHairUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "value"),
            @PlanningVariableReference(variableName = "cutsOwnHair") })
    public boolean isBarber() {
        return barber;
    }

    public void setBarber(boolean barber) {
        this.barber = barber;
    }

    @CustomShadowVariable(variableListenerRef = @PlanningVariableReference(variableName = "barber"))
    public boolean isCutsOwnHair() {
        return cutsOwnHair;
    }

    public void setCutsOwnHair(boolean cutsOwnHair) {
        this.cutsOwnHair = cutsOwnHair;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    // ************************************************************************
    // Static inner classes
    // ************************************************************************

    public static class BarberAndCutsOwnHairUpdatingVariableListener
            extends VariableListenerAdapter<TestdataCyclicReferencedShadowedEntity> {

        @Override
        public void afterEntityAdded(ScoreDirector scoreDirector, TestdataCyclicReferencedShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        @Override
        public void afterVariableChanged(ScoreDirector scoreDirector, TestdataCyclicReferencedShadowedEntity entity) {
            updateShadow(entity, scoreDirector);
        }

        private void updateShadow(TestdataCyclicReferencedShadowedEntity entity, ScoreDirector scoreDirector) {
            // The barber cuts the hair of everyone in the village who does not cut his/her own hair
            // Does the barber cut his own hair?
            TestdataValue value = entity.getValue();
            boolean barber = !entity.isCutsOwnHair();
            scoreDirector.beforeVariableChanged(entity, "barber");
            entity.setBarber(value != null && barber);
            scoreDirector.afterVariableChanged(entity, "barber");
            scoreDirector.beforeVariableChanged(entity, "cutsOwnHair");
            entity.setCutsOwnHair(value != null && !barber);
            scoreDirector.afterVariableChanged(entity, "cutsOwnHair");
        }

    }

}
