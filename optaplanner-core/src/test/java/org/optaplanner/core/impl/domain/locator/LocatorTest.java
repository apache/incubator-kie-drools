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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.optaplanner.core.api.domain.locator.LocationStrategyType;
import org.optaplanner.core.impl.testdata.domain.locator.TestdataObjectId;

import static org.junit.Assert.assertNull;

public class LocatorTest {

    public final ExpectedException expectedException = ExpectedException.none();

    private Locator locator;

    @Before
    public void setUpLocator() {
        locator = new Locator(new LocationStrategyResolver(LocationStrategyType.PLANNING_ID_OR_NONE));
    }

    @Test
    public void locateNull() {
        assertNull(locator.locateWorkingObject(null));
    }

    @Test
    public void resetWorkingObjects() {
        TestdataObjectId o = new TestdataObjectId(0);
        TestdataObjectId p = new TestdataObjectId(1);
        // the objects should be added during the reset
        locator.resetWorkingObjects(Arrays.asList(o, p));
        // so it's possible to locate and remove them
        Assert.assertSame(o, locator.locateWorkingObject(new TestdataObjectId(0)));
        Assert.assertSame(p, locator.locateWorkingObject(new TestdataObjectId(1)));
        locator.removeWorkingObject(o);
        locator.removeWorkingObject(p);
    }

    @Test
    public void clearWorkingObjects() {
        locator.resetWorkingObjects(Collections.emptyList());
        locator.addWorkingObject("");
        locator.clearWorkingObjects();
        expectedException.expect(NullPointerException.class);
        locator.addWorkingObject("");
    }

}
