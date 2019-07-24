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

package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.feel.lang.ast.BaseNode;

public class DDTAInputEntry {

    private final List<BaseNode> uts;
    private final List<Interval> intervals;

    public DDTAInputEntry(List<BaseNode> uts, List<Interval> intervals) {
        this.uts = uts;
        this.intervals = intervals;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(intervals);
        return builder.toString();
    }

    public List<BaseNode> getUts() {
        return uts;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public boolean includes(DDTAInputEntry other) {
        List<Interval> otherIntervals = new ArrayList<>(other.intervals);
        for (Interval i : intervals) {
            List<Interval> includedInI = new ArrayList<>();
            for (Interval o : otherIntervals) {
                if (i.includes(o)) {
                    includedInI.add(o);
                }
            }
            otherIntervals.removeAll(includedInI);
        }
        return otherIntervals.isEmpty();
    }

    public boolean adjOrOverlap(DDTAInputEntry other) {
        return Interval.adjOrOverlap(this.intervals, other.intervals);
    }

}
