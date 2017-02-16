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

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectEquals;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectEqualsNoHashCode;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectEqualsSubclass;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectNoId;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataSolutionLocationStrategyEquality;

import static org.junit.Assert.*;

public class LocationStrategyEqualityTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private Locator locator;

    @Before
    public void setUpLocator() {
        SolutionDescriptor<TestdataSolutionLocationStrategyEquality> solutionDescriptor
                = SolutionDescriptor.buildSolutionDescriptor(TestdataSolutionLocationStrategyEquality.class);
        locator = new Locator(solutionDescriptor);
        locator.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        locator.addWorkingObject(object);
        locator.removeWorkingObject(object);
        // the removed object cannot be located
        assertNull(locator.locateWorkingObject(object));
    }

    @Test
    public void addWithEqualsInSuperclass() {
        TestdataObjectEqualsSubclass object = new TestdataObjectEqualsSubclass(0);
        locator.addWorkingObject(object);
        assertSame(object, locator.locateWorkingObject(object));
    }

    @Test
    public void addWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("override equals");
        expectedException.expectMessage(TestdataObjectNoId.class.getSimpleName());
        locator.addWorkingObject(object);
    }

    @Test
    public void addWithoutHashCode() {
        TestdataObjectEqualsNoHashCode object = new TestdataObjectEqualsNoHashCode(0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("overrides hashCode");
        expectedException.expectMessage(TestdataObjectEqualsNoHashCode.class.getSimpleName());
        locator.addWorkingObject(object);
    }

    @Test
    public void removeWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("override equals");
        expectedException.expectMessage(TestdataObjectNoId.class.getSimpleName());
        locator.removeWorkingObject(object);
    }

    @Test
    public void addEqualObjects() {
        TestdataObjectEquals object = new TestdataObjectEquals(2);
        locator.addWorkingObject(object);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(object.toString());
        locator.addWorkingObject(new TestdataObjectEquals(2));
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("differ");
        locator.removeWorkingObject(object);
    }

    @Test
    public void locateWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(1);
        locator.addWorkingObject(object);
        assertSame(object, locator.locateWorkingObject(new TestdataObjectEquals(1)));
    }

    @Test
    public void locateWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("override equals");
        locator.locateWorkingObject(object);
    }

    @Test
    public void locateWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        assertNull(locator.locateWorkingObject(object));
    }

}
