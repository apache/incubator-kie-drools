/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.validation.dtanalysis;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.validation.dtanalysis.model.Bound;
import org.kie.dmn.validation.dtanalysis.model.Domain;
import org.kie.dmn.validation.dtanalysis.model.Interval;

import static org.assertj.core.api.Assertions.assertThat;

class IntervalTest {

    @Test
    void flatten() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED, 0, 0);
        Interval c = new Interval(RangeBoundary.CLOSED, 3, 4, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(c, b, a));
        assertThat(result).contains(new Interval(RangeBoundary.CLOSED, 0, 4, RangeBoundary.CLOSED, 0, 0));
    }

    @Test
    void flatten2() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED, 0, 0);
        Interval c = new Interval(RangeBoundary.OPEN, 3, 4, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(c, b, a));
        assertThat(result).contains(new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 0, 0),
                                    new Interval(RangeBoundary.OPEN, 3, 4, RangeBoundary.CLOSED, 0, 0));
    }

    @Test
    void flatten3() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.CLOSED, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED, 0, 0);
        Interval c = new Interval(RangeBoundary.CLOSED, 3, 4, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(c, b, a));
        assertThat(result).contains(new Interval(RangeBoundary.CLOSED, 0, 4, RangeBoundary.CLOSED, 0, 0));
    }

    @Test
    void invertOverDomain() {
        Interval a = new Interval(RangeBoundary.CLOSED, 0, 3, RangeBoundary.OPEN, 1, 2);
        Interval domain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(a, domain);
        assertThat(result).hasSize(2);
        assertInterval(result.get(0), RangeBoundary.CLOSED, Interval.NEG_INF, 0, RangeBoundary.OPEN, 1, 2);
        assertInterval(result.get(1), RangeBoundary.CLOSED, 3, Interval.POS_INF, RangeBoundary.CLOSED, 1, 2);
    }

    @Test
    void invertOverDomain2() {
        Interval a = new Interval(RangeBoundary.CLOSED, "i", "o", RangeBoundary.OPEN, 9, 8);
        Interval domain = new Interval(RangeBoundary.CLOSED, "a", "u", RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(a, domain);
        assertThat(result).hasSize(2);
        assertInterval(result.get(0), RangeBoundary.CLOSED, "a", "i", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(1), RangeBoundary.CLOSED, "o", "u", RangeBoundary.CLOSED, 9, 8);
    }

    @Test
    void invertOverDomainList1() {
        Interval i0 = new Interval(RangeBoundary.CLOSED, "a", "a", RangeBoundary.CLOSED, 9, 8);
        Interval i1 = new Interval(RangeBoundary.CLOSED, "e", "e", RangeBoundary.CLOSED, 9, 8);
        Interval domain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(Arrays.asList(i0, i1), domain);
        assertThat(result).hasSize(3);
        assertInterval(result.get(0), RangeBoundary.CLOSED, Interval.NEG_INF, "a", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(1), RangeBoundary.OPEN, "a", "e", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(2), RangeBoundary.OPEN, "e", Interval.POS_INF, RangeBoundary.CLOSED, 9, 8);
    }

    @Test
    void invertOverDomainList2() {
        Interval i0 = new Interval(RangeBoundary.CLOSED, "a", "e", RangeBoundary.OPEN, 9, 8);
        Interval i1 = new Interval(RangeBoundary.CLOSED, "e", "i", RangeBoundary.CLOSED, 9, 8);
        Interval domain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(Arrays.asList(i0, i1), domain);
        assertThat(result).hasSize(2);
        assertInterval(result.get(0), RangeBoundary.CLOSED, Interval.NEG_INF, "a", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(1), RangeBoundary.OPEN, "i", Interval.POS_INF, RangeBoundary.CLOSED, 9, 8);
    }

    @Test
    void invertOverDomainList3() {
        Interval i0 = new Interval(RangeBoundary.CLOSED, "a", "e", RangeBoundary.OPEN, 9, 8);
        Interval i1 = new Interval(RangeBoundary.OPEN, "e", "i", RangeBoundary.CLOSED, 9, 8);
        Interval domain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(Arrays.asList(i0, i1), domain);
        assertThat(result).hasSize(3);
        assertInterval(result.get(0), RangeBoundary.CLOSED, Interval.NEG_INF, "a", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(1), RangeBoundary.CLOSED, "e", "e", RangeBoundary.CLOSED, 9, 8);
        assertInterval(result.get(2), RangeBoundary.OPEN, "i", Interval.POS_INF, RangeBoundary.CLOSED, 9, 8);
    }

    @Test
    void invertOverDomainList4() {
        Interval i0 = new Interval(RangeBoundary.CLOSED, "a", "a", RangeBoundary.CLOSED, 9, 8);
        Interval i1 = new Interval(RangeBoundary.CLOSED, "e", "e", RangeBoundary.CLOSED, 9, 8);
        Interval i2 = new Interval(RangeBoundary.CLOSED, "i", "i", RangeBoundary.CLOSED, 9, 8);
        Interval domain = new Interval(RangeBoundary.CLOSED, Interval.NEG_INF, Interval.POS_INF, RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.invertOverDomain(Arrays.asList(i0, i1, i2), domain);
        assertThat(result).hasSize(4);
        assertInterval(result.get(0), RangeBoundary.CLOSED, Interval.NEG_INF, "a", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(1), RangeBoundary.OPEN, "a", "e", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(2), RangeBoundary.OPEN, "e", "i", RangeBoundary.OPEN, 9, 8);
        assertInterval(result.get(3), RangeBoundary.OPEN, "i", Interval.POS_INF, RangeBoundary.CLOSED, 9, 8);
    }

    private static void assertInterval(Interval interval, RangeBoundary lowType, Comparable<?> lowValue, Comparable<?> hiValue, RangeBoundary hiType, int rule, int col) {
        assertThat(interval.getLowerBound()).isEqualTo(new Bound(lowValue, lowType, interval));
        assertThat(interval.getUpperBound()).isEqualTo(new Bound(hiValue, hiType, interval));
        assertThat(interval.getRule()).isEqualTo(rule);
        assertThat(interval.getCol()).isEqualTo(col);
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
    void humanFriendlyContinuous() {
        Interval domainInterval = new Interval(RangeBoundary.CLOSED, 0, 100, RangeBoundary.CLOSED, 0, 0);
        DummyDomain domain = new DummyDomain(domainInterval, Collections.emptyList());

        assertThat(new Interval(RangeBoundary.CLOSED, 1, 100, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain)).startsWith(">=");
        assertThat(new Interval(RangeBoundary.OPEN  , 1, 100, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain)).startsWith(">");
        assertThat(new Interval(RangeBoundary.OPEN  , 1, 100, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain)).doesNotStartWith(">=");
        
        assertThat(new Interval(RangeBoundary.CLOSED, 0, 99, RangeBoundary.CLOSED, 0, 0).asHumanFriendly(domain)).startsWith("<=");
        assertThat(new Interval(RangeBoundary.CLOSED, 0, 99, RangeBoundary.OPEN  , 0, 0).asHumanFriendly(domain)).startsWith("<");
        assertThat(new Interval(RangeBoundary.CLOSED, 0, 99, RangeBoundary.OPEN  , 0, 0).asHumanFriendly(domain)).doesNotStartWith("<=");
    }

    @Test
    void flatten4() {
        Interval a = new Interval(RangeBoundary.CLOSED, LocalDate.parse("2021-01-01"), LocalDate.parse("2021-03-31"), RangeBoundary.CLOSED, 0, 0);
        Interval b = new Interval(RangeBoundary.CLOSED, LocalDate.parse("2021-04-01"), LocalDate.parse("2021-04-30"), RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(b, a));
        assertThat(result).containsExactly(new Interval(RangeBoundary.CLOSED, LocalDate.parse("2021-01-01"), LocalDate.parse("2021-04-30"), RangeBoundary.CLOSED, 0, 0));
    }

    @Test
    void noFlatten() {
        Interval a = new Interval(RangeBoundary.CLOSED, LocalDate.parse("2021-01-01"), LocalDate.parse("2021-03-31"), RangeBoundary.CLOSED, 0, 0);
        Interval b = new Interval(RangeBoundary.OPEN, LocalDate.parse("2021-04-01"), LocalDate.parse("2021-04-30"), RangeBoundary.CLOSED, 0, 0);

        List<Interval> result = Interval.flatten(Arrays.asList(b, a));
        assertThat(result).containsExactly(a, b);
    }
}
