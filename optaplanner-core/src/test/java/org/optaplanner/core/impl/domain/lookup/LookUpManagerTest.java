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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectIntegerId;

import static org.junit.Assert.*;

public class LookUpManagerTest {

    public final ExpectedException expectedException = ExpectedException.none();

    private LookUpManager lookUpManager;

    @Before
    public void setUpLookUpManager() {
        lookUpManager = new LookUpManager(new LookUpStrategyResolver(LookUpStrategyType.PLANNING_ID_OR_NONE));
    }

    @Test
    public void lookUpNull() {
        assertNull(lookUpManager.lookUpWorkingObject(null));
    }

    @Test
    public void resetWorkingObjects() {
        TestdataObjectIntegerId o = new TestdataObjectIntegerId(0);
        TestdataObjectIntegerId p = new TestdataObjectIntegerId(1);
        // The objects should be added during the reset
        lookUpManager.resetWorkingObjects(Arrays.asList(o, p));
        // So it's possible to look up and remove them
        Assert.assertSame(o, lookUpManager.lookUpWorkingObject(new TestdataObjectIntegerId(0)));
        Assert.assertSame(p, lookUpManager.lookUpWorkingObject(new TestdataObjectIntegerId(1)));
        lookUpManager.removeWorkingObject(o);
        lookUpManager.removeWorkingObject(p);
    }

    @Test
    public void clearWorkingObjects() {
        lookUpManager.resetWorkingObjects(Collections.emptyList());
        lookUpManager.addWorkingObject("");
        lookUpManager.clearWorkingObjects();
        expectedException.expect(NullPointerException.class);
        lookUpManager.addWorkingObject("");
    }

}
