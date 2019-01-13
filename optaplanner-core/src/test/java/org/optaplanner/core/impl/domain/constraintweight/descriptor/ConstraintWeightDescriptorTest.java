/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.constraintweight.descriptor;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.TestdataConstraintConfigurationSolution;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended.TestdataExtendedConstraintConfiguration;
import org.optaplanner.core.impl.testdata.domain.constraintconfiguration.extended.TestdataExtendedConstraintConfigurationSolution;

import static org.junit.Assert.*;

public class ConstraintWeightDescriptorTest {

    @Test
    public void extractionFunction() {
        SolutionDescriptor<TestdataConstraintConfigurationSolution> solutionDescriptor
                = TestdataConstraintConfigurationSolution.buildSolutionDescriptor();
        ConstraintConfigurationDescriptor<TestdataConstraintConfigurationSolution> constraintConfigurationDescriptor
                = solutionDescriptor.getConstraintConfigurationDescriptor();

        ConstraintWeightDescriptor<TestdataConstraintConfigurationSolution> firstWeightDescriptor
                = constraintConfigurationDescriptor.getConstraintWeightDescriptor("firstWeight");
        assertEquals(TestdataConstraintConfigurationSolution.class.getPackage().getName(),
                firstWeightDescriptor.getConstraintPackage());
        assertEquals("First weight", firstWeightDescriptor.getConstraintName());

        ConstraintWeightDescriptor<TestdataConstraintConfigurationSolution> secondWeightDescriptor
                = constraintConfigurationDescriptor.getConstraintWeightDescriptor("secondWeight");
        assertEquals("packageOverwrittenOnField",
                secondWeightDescriptor.getConstraintPackage());
        assertEquals("Second weight", secondWeightDescriptor.getConstraintName());

        TestdataConstraintConfigurationSolution solution = new TestdataConstraintConfigurationSolution("solution");
        TestdataConstraintConfiguration constraintConfiguration = new TestdataConstraintConfiguration("constraintConfiguration");
        constraintConfiguration.setFirstWeight(SimpleScore.ZERO);
        constraintConfiguration.setSecondWeight(SimpleScore.of(7));
        solution.setConstraintConfiguration(constraintConfiguration);

        assertSame(constraintConfiguration, solutionDescriptor.getConstraintConfigurationMemberAccessor().executeGetter(solution));
        assertEquals(SimpleScore.ZERO, firstWeightDescriptor.createExtractor().apply(solution));
        assertEquals(SimpleScore.of(7), secondWeightDescriptor.createExtractor().apply(solution));
    }

    @Test
    public void extractionFunctionExtended() {
        SolutionDescriptor<TestdataExtendedConstraintConfigurationSolution> solutionDescriptor
                = TestdataExtendedConstraintConfigurationSolution.buildExtendedSolutionDescriptor();
        ConstraintConfigurationDescriptor<TestdataExtendedConstraintConfigurationSolution> constraintConfigurationDescriptor
                = solutionDescriptor.getConstraintConfigurationDescriptor();

        ConstraintWeightDescriptor<TestdataExtendedConstraintConfigurationSolution> firstWeightDescriptor
                = constraintConfigurationDescriptor.getConstraintWeightDescriptor("firstWeight");
        assertEquals(TestdataConstraintConfigurationSolution.class.getPackage().getName(),
                firstWeightDescriptor.getConstraintPackage());
        assertEquals("First weight", firstWeightDescriptor.getConstraintName());

        ConstraintWeightDescriptor<TestdataExtendedConstraintConfigurationSolution> secondWeightDescriptor
                = constraintConfigurationDescriptor.getConstraintWeightDescriptor("secondWeight");
        assertEquals("packageOverwrittenOnField",
                secondWeightDescriptor.getConstraintPackage());
        assertEquals("Second weight", secondWeightDescriptor.getConstraintName());

        ConstraintWeightDescriptor<TestdataExtendedConstraintConfigurationSolution> thirdWeightDescriptor
                = constraintConfigurationDescriptor.getConstraintWeightDescriptor("thirdWeight");
        assertEquals(TestdataExtendedConstraintConfigurationSolution.class.getPackage().getName(),
                thirdWeightDescriptor.getConstraintPackage());
        assertEquals("Third weight", thirdWeightDescriptor.getConstraintName());

        TestdataExtendedConstraintConfigurationSolution solution = new TestdataExtendedConstraintConfigurationSolution("solution");
        TestdataExtendedConstraintConfiguration constraintConfiguration = new TestdataExtendedConstraintConfiguration("constraintConfiguration");
        constraintConfiguration.setFirstWeight(SimpleScore.ZERO);
        constraintConfiguration.setSecondWeight(SimpleScore.of(7));
        constraintConfiguration.setThirdWeight(SimpleScore.of(9));
        solution.setConstraintConfiguration(constraintConfiguration);

        assertSame(constraintConfiguration, solutionDescriptor.getConstraintConfigurationMemberAccessor().executeGetter(solution));
        assertEquals(SimpleScore.ZERO, firstWeightDescriptor.createExtractor().apply(solution));
        assertEquals(SimpleScore.of(7), secondWeightDescriptor.createExtractor().apply(solution));
        assertEquals(SimpleScore.of(9), thirdWeightDescriptor.createExtractor().apply(solution));
    }


}
