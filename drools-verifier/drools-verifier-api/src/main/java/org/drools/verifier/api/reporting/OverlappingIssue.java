/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.api.reporting;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.drools.verifier.api.reporting.model.Interval;

public class OverlappingIssue
        extends Issue {

    private List<Interval> intervals;
    private boolean containsAnyValueField;
    private Map<Integer, String> rhsValues;

    public OverlappingIssue() {
    }

    public OverlappingIssue(final Severity severity,
                            final CheckType checkType,
                            final List<Interval> intervals,
                            final boolean containsAnyValueField,
                            final Map<Integer, String> rhsValues,
                            final Set<Integer> rowNumbers) {
        super(severity,
              checkType,
              rowNumbers);
        this.intervals = intervals;
        this.containsAnyValueField = containsAnyValueField;
        this.rhsValues = rhsValues;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public Map<Integer, String> getRhsValues() {
        return rhsValues;
    }

    public boolean isContainsAnyValueField() {
        return containsAnyValueField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OverlappingIssue that = (OverlappingIssue) o;
        return containsAnyValueField == that.containsAnyValueField &&
                Objects.equals(intervals, that.intervals) &&
                Objects.equals(rhsValues, that.rhsValues);
    }

    @Override
    public int hashCode() {
        int result = ~~super.hashCode();
        result = 31 * result + (containsAnyValueField ? 1 : 0);
        result = 31 * result + (intervals != null ? ~~intervals.hashCode() : 0);
        result = 31 * result + (rhsValues != null ? ~~rhsValues.hashCode() : 0);
        return ~~result;
    }
}
