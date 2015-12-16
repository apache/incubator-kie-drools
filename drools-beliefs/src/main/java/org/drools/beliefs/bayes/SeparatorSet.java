/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.Graph;
import org.drools.beliefs.graph.GraphNode;
import org.drools.core.util.bitmask.OpenBitSet;

public class SeparatorSet implements Comparable<SeparatorSet> {
    private int        id1;
    private OpenBitSet clique1;

    private int        id2;
    private OpenBitSet clique2;

    private OpenBitSet intersection;
    private int        mass; // number of vertices that are in the intersects of clique1 and clique2
    private int        cost; // product of the weight of the vertices that are in the intersects of clique1 and clique2

    public SeparatorSet(OpenBitSet clique1, int id1, OpenBitSet clique2, int id2, Graph<BayesVariable> graph) {
        this.id1 = id1;
        this.clique1 = clique1;

        this.clique2 = clique2;
        this.id2 = id2;

        intersection = (OpenBitSet) clique1.clone();
        intersection.and(clique2);

        mass = (int) intersection.cardinality();


        cost = 1;
        if (mass > 0) {
            for (int i = intersection.nextSetBit(0); i >= 0; i = intersection.nextSetBit(i + 1)) {
                GraphNode<BayesVariable> v = graph.getNode(i);
                cost *= Math.abs(v.getContent().getOutcomes().length);
            }
        }
    }

    public int getId1() {
        return id1;
    }

    public OpenBitSet getClique1() {
        return clique1;
    }

    public OpenBitSet getClique2() {
        return clique2;
    }

    public int getId2() {
        return id2;
    }

    public OpenBitSet getIntersection() {
        return intersection;
    }

    public int getMass() {
        return mass;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public int compareTo(SeparatorSet o) {
        if (this == o) { return 0; }
        if (mass != o.mass) {
            return o.mass - mass;
        } else if (cost != o.cost) {
            return o.cost - cost;
        } else {
//            int j = o.intersection.nextSetBit(0);
//            for (int i = intersection.nextSetBit(0); i >= 0; i = intersection.nextSetBit(i + 1)) {
//                if (i != j) {
//                    return i - j;
//                }
//                j = o.intersection.nextSetBit(j + 1);
//
//            }
            // while they are the same, we want the arbitrary result to be deterministic
            // in this case iterate the pairs and return the one that has the fist bit set and the other doesn't.
            int j = o.clique1.nextSetBit(0);
            for (int i = clique1.nextSetBit(0); i >= 0; i = clique1.nextSetBit(i + 1)) {
                if (i != j) {
                    return i - j;
                }
                j = o.clique1.nextSetBit(j + 1);

            }

            j = o.clique2.nextSetBit(0);
            for (int i = clique2.nextSetBit(0); i >= 0; i = clique2.nextSetBit(i + 1)) {
                if (i != j) {
                    return i - j;
                }
                j = o.clique2.nextSetBit(j + 1);

            }
        }
        return 0; // the two pairs of cliques are the same
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        SeparatorSet separatorSet = (SeparatorSet) o;

        if (cost != separatorSet.cost) { return false; }
        if (mass != separatorSet.mass) { return false; }
        if (!clique1.equals(separatorSet.clique1)) { return false; }
        if (!clique2.equals(separatorSet.clique2)) { return false; }
        if (!intersection.equals(separatorSet.intersection)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = clique1.hashCode();
        result = 31 * result + clique2.hashCode();
        result = 31 * result + intersection.hashCode();
        result = 31 * result + mass;
        result = 31 * result + cost;
        return result;
    }

    @Override
    public String toString() {
        return "SepSet{" +
               "clique1=" + clique1 +
               ", clique2=" + clique2 +
               ", intersection=" + intersection +
               ", mass=" + mass +
               ", cost=" + cost +
               '}';
    }
}
