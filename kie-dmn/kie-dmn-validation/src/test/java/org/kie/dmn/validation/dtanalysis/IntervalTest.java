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
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.Domain;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntervalTest {

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

    @Test
    public void testInvertOverDomain() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 1, 2);
        Interval domain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(a, domain);
        assertThat(result, hasSize(2));
        assertInterval(result.get(0), RangeBoundary.CLOSED, Interval.NEG_INF, 0, RangeBoundary.OPEN, 1, 2);
        assertInterval(result.get(1), RangeBoundary.CLOSED, 3, Interval.POS_INF, RangeBoundary.CLOSED, 1, 2);
    }

    @Test
    public void testInvertOverDomain2() {
        Interval a = new Interval(RangeBoundary.CLOSED, "i", "o", RangeBoundary.OPEN, 9, 8);
        Interval domain = new Interval(RangeBoundary.CLOSED, "a", "u", RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(a, domain);
        assertThat(result, hasSize(2));
        assertInterval(result.get(0), RangeBoundary.CLOSED, "a", "i", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(1), RangeBoundary.CLOSED, "o", "u", RangeBoundary.CLOSED, 9, 8);
    }

    private static void assertInterval(Interval interval, RangeBoundary lowType, Comparable<?> lowValue, Comparable<?> hiValue, RangeBoundary hiType, int rule, int col) {
        assertThat(interval.getLowerBound(), is(new Bound(lowValue, lowType, interval)));
        assertThat(interval.getUpperBound(), is(new Bound(hiValue, hiType, interval)));
        assertThat(interval.getRule(), is(rule));
        assertThat(interval.getCol(), is(col));
    }

    public static class DummyDomain implements Domain {

        private final Interval domainMinMax;
        private final List discreteValues;

        public DummyDomain(Interval domainMinMax, List discreteValues) {
            this.domainMinMax = domainMinMax;
            this.discreteValues = discreteValues;
        }

        @Override
        public Bound<?> getMin() {
            return domainMinMax.getLowerBound();
        }

        @Override
        public Bound<?> getMax() {
            return domainMinMax.getUpperBound();
        }

        @Override
        public Interval getDomainMinMax() {
            return domainMinMax;
        }

        @Override
        public List getDiscreteValues() {
            return Collections.unmodifiableList(discreteValues);
        }

        @Override
        public boolean isDiscreteDomain() {
            return discreteValues != null && !discreteValues.isEmpty();
        }
    }

    @Test
    public void testHumanFriendlyContinuous() {
        Interval domainInterval = new Interval(RangeBoundary.CLOSED, 0, 100, RangeBoundary.CLOSED, 0, 0);
        DummyDomain domain = new DummyDomain(domainInterval, Collections.emptyList());

        assertThat(new Interval(RangeBoundary.CLOSED, 1, 100, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain), startsWith(">="));
        assertThat(new Interval(RangeBoundary.OPEN  , 1, 100, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain), startsWith(">"));
        assertThat(new Interval(RangeBoundary.OPEN  , 1, 100, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain), not(startsWith(">=")));
        
        assertThat(new Interval(RangeBoundary.CLOSED, 0, 99, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain), startsWith("<="));
        assertThat(new Interval(RangeBoundary.CLOSED, 0, 99, RangeBoundary.OPEN  , 0, 0).asHumanFriendly(domain), startsWith("<"));
        assertThat(new Interval(RangeBoundary.CLOSED, 0, 99, RangeBoundary.OPEN  , 0, 0).asHumanFriendly(domain), not(startsWith("<=")));
    }
}
