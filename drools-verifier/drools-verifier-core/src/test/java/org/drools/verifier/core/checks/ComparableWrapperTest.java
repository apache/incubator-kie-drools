/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.checks;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class ComparableWrapperTest {

    private final ComparableWrapper theNull = new ComparableWrapper(null);
    private final ComparableWrapper one = new ComparableWrapper(1);
    private final ComparableWrapper ten = new ComparableWrapper(10);
    private final ComparableWrapper min = ComparableWrapper.MIN_VALUE;
    private final ComparableWrapper max = ComparableWrapper.MAX_VALUE;

    @Test
    public void testSorting() {

        final ComparableWrapper[] unsorted = {one, ten, theNull, max, min};
        final ComparableWrapper[] sorted = {min, theNull, one, ten, max};

        Arrays.sort(unsorted);

        Assert.assertArrayEquals(sorted, unsorted);
    }

    @Test
    public void compareTo() {
        Assert.assertEquals(0, theNull.compareTo(theNull));
        Assert.assertEquals(0, one.compareTo(one));
        Assert.assertEquals(0, ten.compareTo(ten));
        Assert.assertEquals(0, min.compareTo(min));
        Assert.assertEquals(0, max.compareTo(max));

        Assert.assertTrue(one.compareTo(theNull) > 0);
        Assert.assertTrue(one.compareTo(ten) < 0);
        Assert.assertTrue(one.compareTo(min) > 0);
        Assert.assertTrue(one.compareTo(max) < 0);

        Assert.assertTrue(ten.compareTo(theNull) > 0);
        Assert.assertTrue(ten.compareTo(one) > 0);
        Assert.assertTrue(ten.compareTo(min) > 0);
        Assert.assertTrue(ten.compareTo(max) < 0);

        Assert.assertTrue(min.compareTo(theNull) < 0);
        Assert.assertTrue(min.compareTo(one) < 0);
        Assert.assertTrue(min.compareTo(ten) < 0);
        Assert.assertTrue(min.compareTo(max) < 0);

        Assert.assertTrue(max.compareTo(theNull) > 0);
        Assert.assertTrue(max.compareTo(one) > 0);
        Assert.assertTrue(max.compareTo(ten) > 0);
        Assert.assertTrue(max.compareTo(min) > 0);
    }
}