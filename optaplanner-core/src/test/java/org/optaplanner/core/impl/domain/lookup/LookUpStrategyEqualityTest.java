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

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEquals;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEqualsNoHashCode;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEqualsSubclass;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectNoId;

import static org.junit.Assert.*;

public class LookUpStrategyEqualityTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private LookUpManager lookUpManager;

    @Before
    public void setUpLookUpManager() {
        lookUpManager = new LookUpManager(new LookUpStrategyResolver(LookUpStrategyType.EQUALITY));
        lookUpManager.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
        // The removed object cannot be looked up
        assertNull(lookUpManager.lookUpWorkingObjectOrReturnNull(object));
    }

    @Test
    public void addWithEqualsInSuperclass() {
        TestdataObjectEqualsSubclass object = new TestdataObjectEqualsSubclass(0);
        lookUpManager.addWorkingObject(object);
        assertSame(object, lookUpManager.lookUpWorkingObject(object));
    }

    @Test
    public void addWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("override the equals() method");
        expectedException.expectMessage(TestdataObjectNoId.class.getSimpleName());
        lookUpManager.addWorkingObject(object);
    }

    @Test
    public void addWithoutHashCode() {
        TestdataObjectEqualsNoHashCode object = new TestdataObjectEqualsNoHashCode(0);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("overrides the hashCode() method");
        expectedException.expectMessage(TestdataObjectEqualsNoHashCode.class.getSimpleName());
        lookUpManager.addWorkingObject(object);
    }

    @Test
    public void removeWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("override the equals() method");
        expectedException.expectMessage(TestdataObjectNoId.class.getSimpleName());
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void addEqualObjects() {
        TestdataObjectEquals object = new TestdataObjectEquals(2);
        lookUpManager.addWorkingObject(object);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(object.toString());
        lookUpManager.addWorkingObject(new TestdataObjectEquals(2));
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("differ");
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void lookUpWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(1);
        lookUpManager.addWorkingObject(object);
        assertSame(object, lookUpManager.lookUpWorkingObject(new TestdataObjectEquals(1)));
    }

    @Test
    public void lookUpWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("override the equals() method");
        lookUpManager.lookUpWorkingObject(object);
    }

    @Test
    public void lookUpWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        assertNull(lookUpManager.lookUpWorkingObjectOrReturnNull(object));
    }

}
