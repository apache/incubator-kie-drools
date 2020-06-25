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

package org.optaplanner.core.impl.domain.variable.inverserelation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.shadow.inverserelation.TestdataInverseRelationEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.inverserelation.TestdataInverseRelationSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.inverserelation.TestdataInverseRelationValue;

public class CollectionInverseVariableListenerTest {

    @Test
    public void normal() {
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        SolutionDescriptor solutionDescriptor = TestdataInverseRelationSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataInverseRelationEntity.class);
        EntityDescriptor shadowEntityDescriptor = solutionDescriptor
                .findEntityDescriptorOrFail(TestdataInverseRelationValue.class);
        ShadowVariableDescriptor entitiesVariableDescriptor = shadowEntityDescriptor.getShadowVariableDescriptor("entities");
        CollectionInverseVariableListener variableListener = new CollectionInverseVariableListener(
                (InverseRelationShadowVariableDescriptor) entitiesVariableDescriptor,
                entityDescriptor.getGenuineVariableDescriptor("value"));

        TestdataInverseRelationValue val1 = new TestdataInverseRelationValue("1");
        TestdataInverseRelationValue val2 = new TestdataInverseRelationValue("2");
        TestdataInverseRelationValue val3 = new TestdataInverseRelationValue("3");
        TestdataInverseRelationEntity a = new TestdataInverseRelationEntity("a", val1);
        TestdataInverseRelationEntity b = new TestdataInverseRelationEntity("b", val1);
        TestdataInverseRelationEntity c = new TestdataInverseRelationEntity("c", val3);
        TestdataInverseRelationEntity d = new TestdataInverseRelationEntity("d", val3);

        TestdataInverseRelationSolution solution = new TestdataInverseRelationSolution("solution");
        solution.setEntityList(Arrays.asList(a, b, c, d));
        solution.setValueList(Arrays.asList(val1, val2, val3));

        assertThat(val1.getEntities()).containsExactly(a, b);
        assertThat(val2.getEntities()).containsExactly();
        assertThat(val3.getEntities()).containsExactly(c, d);

        variableListener.beforeVariableChanged(scoreDirector, c);
        c.setValue(val2);
        variableListener.afterVariableChanged(scoreDirector, c);

        assertThat(val1.getEntities()).containsExactly(a, b);
        assertThat(val2.getEntities()).containsExactly(c);
        assertThat(val3.getEntities()).containsExactly(d);
    }

}
