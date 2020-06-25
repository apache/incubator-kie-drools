/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import java.util.Comparator;

import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;

/**
 * Investigate deeper nodes first.
 */
public class DepthFirstNodeComparator implements Comparator<ExhaustiveSearchNode> {

    private final boolean scoreBounderEnabled;

    public DepthFirstNodeComparator(boolean scoreBounderEnabled) {
        this.scoreBounderEnabled = scoreBounderEnabled;
    }

    @Override
    public int compare(ExhaustiveSearchNode a, ExhaustiveSearchNode b) {
        // Investigate deeper first
        int aDepth = a.getDepth();
        int bDepth = b.getDepth();
        if (aDepth < bDepth) {
            return -1;
        } else if (aDepth > bDepth) {
            return 1;
        }
        // Investigate better score first (ignore initScore as that's already done by investigate deeper first)
        int scoreComparison = a.getScore().withInitScore(0).compareTo(b.getScore().withInitScore(0));
        if (scoreComparison < 0) {
            return -1;
        } else if (scoreComparison > 0) {
            return 1;
        }
        // Pitfall: score is compared before optimisticBound, because of this mixed ONLY_UP and ONLY_DOWN cases:
        // - Node a has score 0hard/20medium/-50soft and optimisticBound 0hard/+(infinity)medium/-50soft
        // - Node b has score 0hard/0medium/0soft and optimisticBound 0hard/+(infinity)medium/0soft
        // In non-mixed cases, the comparison order is irrelevant.
        if (scoreBounderEnabled) {
            // Investigate better optimistic bound first
            int optimisticBoundComparison = a.getOptimisticBound().compareTo(b.getOptimisticBound());
            if (optimisticBoundComparison < 0) {
                return -1;
            } else if (optimisticBoundComparison > 0) {
                return 1;
            }
        }
        // Investigate higher parent breadth index first (to reduce on the churn on workingSolution)
        long aParentBreadth = a.getParentBreadth();
        long bParentBreadth = b.getParentBreadth();
        if (aParentBreadth < bParentBreadth) {
            return -1;
        } else if (aParentBreadth > bParentBreadth) {
            return 1;
        }
        // Investigate lower breadth index first (to respect ValueSortingManner)
        return Long.compare(b.getBreadth(), a.getBreadth());
    }

}
