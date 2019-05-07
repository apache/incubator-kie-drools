/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.dtanalysis;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class IntervalTest extends AbstractDTAnalysisTest {

    @Test
    public void testFlatten() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED, 0, 0);
        Interval c = new Interval(RangeBoundary.CLOSED, 3, 4, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(c, b, a));
        assertThat(result, contains(new Interval(RangeBoundary.CLOSED, 0, 4, RangeBoundary.CLOSED, 0, 0)));
    }

    @Test
    public void testFlatten2() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED, 0, 0);
        Interval c = new Interval(RangeBoundary.OPEN, 3, 4, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(c, b, a));
        assertThat(result, contains(new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 0, 0),
                                    new Interval(RangeBoundary.OPEN, 3, 4, RangeBoundary.CLOSED, 0, 0)));
    }

    @Test
    public void testFlatten3() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.CLOSED, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED, 0, 0);
        Interval c = new Interval(RangeBoundary.CLOSED, 3, 4, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(c, b, a));
        assertThat(result, contains(new Interval(RangeBoundary.CLOSED, 0, 4, RangeBoundary.CLOSED, 0, 0)));
    }
}
