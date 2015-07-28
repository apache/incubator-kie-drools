/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.custom;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.shadow.cyclic.TestdataCyclicShadowedSolution;
import org.optaplanner.core.impl.testdata.domain.shadow.extended.TestdataExtendedShadowedChildEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.extended.TestdataExtendedShadowedParentEntity;
import org.optaplanner.core.impl.testdata.domain.shadow.extended.TestdataExtendedShadowedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class CustomVariableListenerTest {

    @Test(expected = IllegalStateException.class) @Ignore // TODO Fix PLANNER-400
    public void cyclic() {
        SolutionDescriptor solutionDescriptor = TestdataCyclicShadowedSolution.buildSolutionDescriptor();
    }

    @Test
    public void extendedZigZag() {
        GenuineVariableDescriptor variableDescriptor = TestdataExtendedShadowedParentEntity.buildVariableDescriptorForValue();
        InnerScoreDirector scoreDirector = PlannerTestUtils.mockScoreDirector(
                variableDescriptor.getEntityDescriptor().getSolutionDescriptor());

        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataExtendedShadowedParentEntity a = new TestdataExtendedShadowedParentEntity("a", null);
        TestdataExtendedShadowedParentEntity b = new TestdataExtendedShadowedParentEntity("b", null);
        TestdataExtendedShadowedChildEntity c = new TestdataExtendedShadowedChildEntity("c", null);

        TestdataExtendedShadowedSolution solution = new TestdataExtendedShadowedSolution("solution");
        solution.setEntityList(Arrays.asList(a, b, c));
        solution.setValueList(Arrays.asList(val1, val2, val3));
        scoreDirector.setWorkingSolution(solution);

        scoreDirector.beforeVariableChanged(variableDescriptor, a);
        a.setValue(val1);
        scoreDirector.afterVariableChanged(variableDescriptor, a);
        assertEquals("1/firstShadow", a.getFirstShadow());
        assertEquals(null, a.getThirdShadow());

        scoreDirector.beforeVariableChanged(variableDescriptor, a);
        a.setValue(val3);
        scoreDirector.afterVariableChanged(variableDescriptor, a);
        assertEquals("3/firstShadow", a.getFirstShadow());
        assertEquals(null, a.getThirdShadow());

        scoreDirector.beforeVariableChanged(variableDescriptor, c);
        c.setValue(val1);
        scoreDirector.afterVariableChanged(variableDescriptor, c);
        assertEquals("1/firstShadow", c.getFirstShadow());
        assertEquals("1/firstShadow/secondShadow", c.getSecondShadow());
        assertEquals("1/firstShadow/secondShadow/thirdShadow", c.getThirdShadow());

        scoreDirector.beforeVariableChanged(variableDescriptor, c);
        c.setValue(val3);
        scoreDirector.afterVariableChanged(variableDescriptor, c);
        assertEquals("3/firstShadow", c.getFirstShadow());
        assertEquals("3/firstShadow/secondShadow", c.getSecondShadow());
        assertEquals("3/firstShadow/secondShadow/thirdShadow", c.getThirdShadow());
    }

}
