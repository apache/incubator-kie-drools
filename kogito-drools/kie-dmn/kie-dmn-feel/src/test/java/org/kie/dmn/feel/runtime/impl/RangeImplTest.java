/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.impl;

import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;

public class RangeImplTest {

    @Test
    public void getLowBoundary() {
        final Range.RangeBoundary lowBoundary = Range.RangeBoundary.CLOSED;
        final RangeImpl rangeImpl = new RangeImpl(lowBoundary, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertEquals(lowBoundary, rangeImpl.getLowBoundary());
    }

    @Test
    public void getLowEndPoint() {
        final Integer lowEndPoint = 1;
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, lowEndPoint, 15, Range.RangeBoundary.CLOSED);
        Assert.assertEquals(lowEndPoint, rangeImpl.getLowEndPoint());
    }

    @Test
    public void getHighEndPoint() {
        final Integer highEndPoint = 15;
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 1, highEndPoint, Range.RangeBoundary.CLOSED);
        Assert.assertEquals(highEndPoint, rangeImpl.getHighEndPoint());
    }

    @Test
    public void getHighBoundary() {
        final Range.RangeBoundary highBoundary = Range.RangeBoundary.CLOSED;
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, highBoundary);
        Assert.assertEquals(highBoundary, rangeImpl.getHighBoundary());
    }

    @Test
    public void includes() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertFalse(rangeImpl.includes(-15));
        Assert.assertFalse(rangeImpl.includes(5));
        Assert.assertFalse(rangeImpl.includes(10));
        Assert.assertTrue(rangeImpl.includes(12));
        Assert.assertFalse(rangeImpl.includes(15));
        Assert.assertFalse(rangeImpl.includes(156));

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertTrue(rangeImpl.includes(10));
        Assert.assertTrue(rangeImpl.includes(12));
        Assert.assertFalse(rangeImpl.includes(15));

        rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.CLOSED);
        Assert.assertFalse(rangeImpl.includes(10));
        Assert.assertTrue(rangeImpl.includes(12));
        Assert.assertTrue(rangeImpl.includes(15));

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.CLOSED);
        Assert.assertTrue(rangeImpl.includes(10));
        Assert.assertTrue(rangeImpl.includes(12));
        Assert.assertTrue(rangeImpl.includes(15));
    }

    @Test
    public void equals() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertEquals(rangeImpl, rangeImpl);

        RangeImpl rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertEquals(rangeImpl, rangeImpl2);

        rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl, rangeImpl2);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertNotEquals(rangeImpl, rangeImpl2);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl, rangeImpl2);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl, rangeImpl2);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 17, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl, rangeImpl2);

        rangeImpl = new RangeImpl();
        Assert.assertEquals(rangeImpl, rangeImpl);
    }

    @Test
    public void hashCodeTest() {
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertEquals(rangeImpl.hashCode(), rangeImpl.hashCode());

        RangeImpl rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertEquals(rangeImpl.hashCode(), rangeImpl2.hashCode());

        rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl.hashCode(), rangeImpl2.hashCode());
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.OPEN);
        Assert.assertNotEquals(rangeImpl.hashCode(), rangeImpl2.hashCode());
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl.hashCode(), rangeImpl2.hashCode());
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl.hashCode(), rangeImpl2.hashCode());
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 17, Range.RangeBoundary.CLOSED);
        Assert.assertNotEquals(rangeImpl.hashCode(), rangeImpl2.hashCode());
    }
}