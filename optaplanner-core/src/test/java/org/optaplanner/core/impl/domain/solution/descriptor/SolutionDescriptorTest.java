/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.solution.descriptor;

import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.impl.testdata.domain.extended.unimplemented.TestdataExtendedAbstractSolution;
import org.optaplanner.core.impl.testdata.domain.misannotated.TestdataEntityWithInvalidFactPropertyGetter;

public class SolutionDescriptorTest {

    @Test(expected = IllegalStateException.class)
    public void testSolutionWithFactPropertyGetterHavingAttributes() {
        SolutionDescriptor<TestdataEntityWithInvalidFactPropertyGetter> descriptor =
                new SolutionDescriptor<>(TestdataEntityWithInvalidFactPropertyGetter.class);
        descriptor.processAnnotations(null);
    }

    @Test
    public void testExtendingAbstractSolution() {
        SolutionDescriptor<TestdataExtendedAbstractSolution> descriptor
                = new SolutionDescriptor<>(TestdataExtendedAbstractSolution.class);
        descriptor.processAnnotations(null);
        Assert.assertEquals("Fact collection inherited from abstract class is not registered.",
                1, descriptor.getFactCollectionPropertyAccessorMap().size());
        Assert.assertEquals("Private fact property from the solution class is not registered.",
                1, descriptor.getFactPropertyAccessorMap().size());
    }

}
