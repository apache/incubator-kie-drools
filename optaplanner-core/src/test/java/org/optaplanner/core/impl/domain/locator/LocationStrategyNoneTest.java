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
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectId;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectMultipleIds;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectNoId;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataSolutionLocationStrategyNone;

public class LocationStrategyNoneTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();
    private Locator<TestdataSolutionLocationStrategyNone> locator;

    @Before
    public void setUpLocator() {
        SolutionDescriptor<TestdataSolutionLocationStrategyNone> solutionDescriptor
                = SolutionDescriptor.buildSolutionDescriptor(TestdataSolutionLocationStrategyNone.class);
        locator = new Locator<>(solutionDescriptor);
        locator.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithId() {
        TestdataObjectId object = new TestdataObjectId(0);
        locator.addWorkingObject(object);
        locator.removeWorkingObject(object);
    }

    @Test
    public void addWithNullId() {
        TestdataObjectId object = new TestdataObjectId(null);
        // not checked
        locator.addWorkingObject(object);
    }

    @Test
    public void removeWithNullId() {
        TestdataObjectId object = new TestdataObjectId(null);
        // not checked
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
        // not checked
        locator.addWorkingObject(new TestdataObjectId(2));
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectId object = new TestdataObjectId(0);
        // not checked
        locator.removeWorkingObject(object);
    }

    @Test
    public void locateWithId() {
        TestdataObjectId object = new TestdataObjectId(0);
        locator.addWorkingObject(object);
        // not allowed
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be located");
        locator.locateWorkingObject(object);
    }

    @Test
    public void locateWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        locator.addWorkingObject(object);
        // not allowed
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be located");
        locator.locateWorkingObject(object);
    }

    @Test
    public void locateWithoutAdding() {
        TestdataObjectId object = new TestdataObjectId(0);
        // not allowed
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be located");
        locator.locateWorkingObject(object);
    }

    @Test
    public void addWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        // not checked
        locator.addWorkingObject(object);
    }

    @Test
    public void removeWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        // not checked
        locator.removeWorkingObject(object);
    }
}
