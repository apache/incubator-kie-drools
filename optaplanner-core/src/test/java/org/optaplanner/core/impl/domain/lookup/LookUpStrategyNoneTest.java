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
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectIntegerId;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectMultipleIds;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectNoId;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectPrimitiveIntId;

public class LookUpStrategyNoneTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private LookUpManager lookUpManager;

    @Before
    public void setUpLookUpManager() {
        lookUpManager = new LookUpManager(new LookUpStrategyResolver(LookUpStrategyType.NONE));
        lookUpManager.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithIntegerId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void addRemoveWithPrimitiveIntId() {
        TestdataObjectPrimitiveIntId object = new TestdataObjectPrimitiveIntId(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void addWithNullId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(null);
        // not checked
        lookUpManager.addWorkingObject(object);
    }

    @Test
    public void removeWithNullId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(null);
        // not checked
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void addWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        lookUpManager.addWorkingObject(object);
    }

    @Test
    public void removeWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void addSameIdTwice() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(2);
        lookUpManager.addWorkingObject(object);
        // not checked
        lookUpManager.addWorkingObject(new TestdataObjectIntegerId(2));
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        // not checked
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void lookUpWithId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        lookUpManager.addWorkingObject(object);
        // not allowed
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be looked up");
        lookUpManager.lookUpWorkingObject(object);
    }

    @Test
    public void lookUpWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        lookUpManager.addWorkingObject(object);
        // not allowed
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be looked up");
        lookUpManager.lookUpWorkingObject(object);
    }

    @Test
    public void lookUpWithoutAdding() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        // not allowed
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("cannot be looked up");
        lookUpManager.lookUpWorkingObject(object);
    }

    @Test
    public void addWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        // not checked
        lookUpManager.addWorkingObject(object);
    }

    @Test
    public void removeWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        // not checked
        lookUpManager.removeWorkingObject(object);
    }
}
