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
import org.optaplanner.core.api.domain.locator.PlanningId;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectId;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectIdSubclass;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectMultipleIds;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectNoId;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataSolutionLocationStrategyIdOrNone;

import static org.junit.Assert.*;

public class LocationStrategyIdOrNoneTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private Locator<TestdataSolutionLocationStrategyIdOrNone> locator;

    @Before
    public void setUpLocator() {
        SolutionDescriptor<TestdataSolutionLocationStrategyIdOrNone> solutionDescriptor
                = SolutionDescriptor.buildSolutionDescriptor(TestdataSolutionLocationStrategyIdOrNone.class);
        locator = new Locator<>(solutionDescriptor);
        locator.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithId() {
        TestdataObjectId object = new TestdataObjectId(0);
        locator.addWorkingObject(object);
        locator.removeWorkingObject(object);
        // the removed object cannot be located
        assertNull(locator.locateWorkingObject(object));
    }

    @Test
    public void addWithNullIdInSuperclass() {
        TestdataObjectId object = new TestdataObjectIdSubclass(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must not be null");
        expectedException.expectMessage(TestdataObjectIdSubclass.class.getCanonicalName());
        expectedException.expectMessage(object.toString());
        locator.addWorkingObject(object);
    }

    @Test
    public void removeWithNullId() {
        TestdataObjectId object = new TestdataObjectId(null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("must not be null");
        locator.removeWorkingObject(object);
    }

    @Test
    public void addWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        locator.addWorkingObject(object);
    }

    @Test
    public void removeWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        locator.removeWorkingObject(object);
    }

    @Test
    public void addSameIdTwice() {
        TestdataObjectId object = new TestdataObjectId(2);
        locator.addWorkingObject(object);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(" have the same planningId ");
        expectedException.expectMessage(object.toString());
        locator.addWorkingObject(new TestdataObjectId(2));
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectId object = new TestdataObjectId(0);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("differ");
        locator.removeWorkingObject(object);
    }

    @Test
    public void locateWithId() {
        TestdataObjectId object = new TestdataObjectId(1);
        locator.addWorkingObject(object);
        assertSame(object, locator.locateWorkingObject(new TestdataObjectId(1)));
    }

    @Test
    public void locateWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        locator.addWorkingObject(object);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be located");
        locator.locateWorkingObject(object);
    }

    @Test
    public void locateWithoutAdding() {
        TestdataObjectId object = new TestdataObjectId(0);
        assertNull(locator.locateWorkingObject(object));
    }

    @Test
    public void addWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("3 members");
        expectedException.expectMessage(PlanningId.class.getSimpleName());
        locator.addWorkingObject(object);
    }

    @Test
    public void removeWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("3 members");
        expectedException.expectMessage(PlanningId.class.getSimpleName());
        locator.removeWorkingObject(object);
    }
}
