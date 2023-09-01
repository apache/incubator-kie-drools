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
package org.kie.dmn.validation.dtanalysis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.util.Generated;

public class Overlap {

    private final List<Integer> rules = new ArrayList<>();
    private final Hyperrectangle overlap;

    public Overlap(Collection<Integer> rules, Hyperrectangle overlap) {
        this.rules.addAll(rules);
        this.overlap = overlap;
    }

    public List<Integer> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public Hyperrectangle getOverlap() {
        return overlap;
    }

    @Override
    public String toString() {
        return "Overlap values " +
                overlap +
                " for rules: " +
                rules;
    }

    public int contigousOnDimension(Overlap other) {
        if (!this.rules.equals(other.rules)) {
            return 0;
        }
        for (int d = 0; d < this.overlap.getDimensions(); d++) {
            Interval thisDEdge = this.overlap.getEdges().get(d);
            Interval otherDEdge = other.overlap.getEdges().get(d);
            if (Bound.adOrOver(thisDEdge.getUpperBound(), otherDEdge.getLowerBound()) || Bound.adOrOver(otherDEdge.getUpperBound(), thisDEdge.getLowerBound())) {
                boolean otherDimensionsSame = true; //assume true
                for (int o = 0; o < this.overlap.getDimensions(); o++) {
                    if (o != d) {
                        otherDimensionsSame &= this.overlap.getEdges().get(o).equals(other.overlap.getEdges().get(o));
                    }
                }
                if (otherDimensionsSame) {
                    return d + 1;
                }
            }
        }
        return 0;
    }

    public static Overlap newByMergeOnDimension(Overlap o1, Overlap o2, int dimension) {
        int d = dimension - 1;
        List<Integer> rules = o1.rules;
        Interval[] newOverlapHR = new Interval[o1.getOverlap().getDimensions()];
        for (int o = 0; o < o1.overlap.getDimensions(); o++) {
            if (o != d) {
                newOverlapHR[o] = o1.overlap.getEdges().get(o);
            } else {
                Interval o1MergeEdge = o1.overlap.getEdges().get(o);
                Interval o2MergeEdge = o2.overlap.getEdges().get(o);
                if (Bound.adOrOver(o1MergeEdge.getUpperBound(), o2MergeEdge.getLowerBound())) {
                    Interval newFromBounds = Interval.newFromBounds(o1MergeEdge.getLowerBound(), o2MergeEdge.getUpperBound());
                    newOverlapHR[o] = newFromBounds;
                } else if (Bound.adOrOver(o2MergeEdge.getUpperBound(), o1MergeEdge.getLowerBound())) {
                    Interval newFromBounds = Interval.newFromBounds(o2MergeEdge.getLowerBound(), o1MergeEdge.getUpperBound());
                    newOverlapHR[o] = newFromBounds;
                } else {
                    throw new IllegalStateException();
                }
            }
        }
        Hyperrectangle hr = new Hyperrectangle(o1.overlap.getDimensions(), Arrays.asList(newOverlapHR));
        return new Overlap(rules, hr);
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((overlap == null) ? 0 : overlap.hashCode());
        result = prime * result + rules.hashCode();
        return result;
    }

    @Generated("org.eclipse.jdt.internal.corext.codemanipulation")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Overlap other = (Overlap) obj;
        if (overlap == null) {
            if (other.overlap != null) {
                return false;
            }
        } else if (!overlap.equals(other.overlap)) {
            return false;
        }
        if (!rules.equals(other.rules)) {
            return false;
        }
        return true;
    }

    public String asHumanFriendly(DDTATable ddtaTable) {
        StringBuilder builder = new StringBuilder();
        builder.append("Overlap values: ");
        builder.append(overlap.asHumanFriendly(ddtaTable));
        builder.append(" for rules: ");
        builder.append(rules);
        return builder.toString();
    }

}
