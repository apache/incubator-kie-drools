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
package org.optaplanner.core.impl.domain.lookup;

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
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class LookUpStrategyImmutableTest {

    private final Object internalObject;
    private final Object externalObject;
    private LookUpManager lookUpManager;

    public LookUpStrategyImmutableTest(Object internalObject, Object externalObject) {
        this.internalObject = internalObject;
        this.externalObject = externalObject;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Object[] data() {
        return new Object[][]{
            {true, new Boolean(true)},
            {(byte) 1, new Byte((byte) 1)},
            {(short) 1, new Short((short) 1)},
            {1, new Integer(1)},
            {1L, new Long(1)},
            {0.5f, new Float(0.5f)},
            {0.1d, new Double(0.1d)},
            {BigInteger.ONE, new BigInteger("1")},
            {BigDecimal.ONE, new BigDecimal("1")},
            {'!', new Character((char) 33)},
            {"", new String()},
            {LocalDate.of(1, 2, 3), LocalDate.of(1, 2, 3)},
            {LocalTime.of(1, 2), LocalTime.of(1, 2)},
            {LocalDateTime.of(1, 2, 3, 4, 5), LocalDateTime.of(1, 2, 3, 4, 5)}
        };
    }

    @Before
    public void setUpLookUpManager() {
        lookUpManager = new LookUpManager(new LookUpStrategyResolver(LookUpStrategyType.PLANNING_ID_OR_NONE));
        lookUpManager.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addImmutable() {
        lookUpManager.addWorkingObject(internalObject);
    }

    @Test
    public void removeImmutable() {
        lookUpManager.removeWorkingObject(internalObject);
    }

    @Test
    public void lookUpImmutable() {
        // make sure we are working with different instances
        assertNotSame(internalObject, externalObject);
        // since they are immutable we don't care about which instance is looked up
        assertEquals(internalObject, lookUpManager.lookUpWorkingObject(externalObject));
    }

}
