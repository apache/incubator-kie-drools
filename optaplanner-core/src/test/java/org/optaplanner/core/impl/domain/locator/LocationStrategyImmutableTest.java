/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.locator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataSolutionLocationStrategyIdOrFail;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class LocationStrategyImmutableTest {

    private final Object internalObject;
    private final Object externalObject;
    private Locator locator;

    public LocationStrategyImmutableTest(Object internalObject, Object externalObject) {
        this.internalObject = internalObject;
        this.externalObject = externalObject;
    }

    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[][]{
            {true, new Boolean(true)},
            {(byte) 1, new Byte((byte) 1)},
            {(short) 1, new Short((short) 1)},
            {1, new Integer(1),},
            {1L, new Long(1),},
            {0.5f, new Float(0.5f)},
            {0.1d, new Double(0.1d)},
            {BigInteger.ONE, new BigInteger("1")},
            {BigDecimal.ONE, new BigDecimal("1")},
            {'\n', new Character((char) 10)},
            {"", new String()},
            {LocalDate.of(1, 2, 3), LocalDate.of(1, 2, 3)},
            {LocalTime.of(1, 2), LocalTime.of(1, 2)},
            {LocalDateTime.of(1, 2, 3, 4, 5), LocalDateTime.of(1, 2, 3, 4, 5)}
        };
    }

    @Before
    public void setUpLocator() {
        SolutionDescriptor<TestdataSolutionLocationStrategyIdOrFail> solutionDescriptor
                = SolutionDescriptor.buildSolutionDescriptor(TestdataSolutionLocationStrategyIdOrFail.class);
        locator = new Locator(solutionDescriptor);
        locator.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addImmutable() {
        locator.addWorkingObject(internalObject);
    }

    @Test
    public void removeImmutable() {
        locator.removeWorkingObject(internalObject);
    }

    @Test
    public void locateImmutable() {
        // make sure we are working with different instances
        assertNotSame(internalObject, externalObject);
        // since they are immutable we don't care about which instance is located
        assertEquals(internalObject, locator.locateWorkingObject(externalObject));
    }

}
