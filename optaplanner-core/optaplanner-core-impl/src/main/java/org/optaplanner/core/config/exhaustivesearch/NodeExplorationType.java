/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.exhaustivesearch;

import java.util.Comparator;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.BreadthFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.DepthFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.OptimisticBoundFirstNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.OriginalOrderNodeComparator;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.ScoreFirstNodeComparator;

@XmlEnum
public enum NodeExplorationType {
    ORIGINAL_ORDER,
    DEPTH_FIRST,
    BREADTH_FIRST,
    SCORE_FIRST,
    OPTIMISTIC_BOUND_FIRST;

    public Comparator<ExhaustiveSearchNode> buildNodeComparator(boolean scoreBounderEnabled) {
        switch (this) {
            case ORIGINAL_ORDER:
                return new OriginalOrderNodeComparator();
            case DEPTH_FIRST:
                return new DepthFirstNodeComparator(scoreBounderEnabled);
            case BREADTH_FIRST:
                return new BreadthFirstNodeComparator(scoreBounderEnabled);
            case SCORE_FIRST:
                return new ScoreFirstNodeComparator(scoreBounderEnabled);
            case OPTIMISTIC_BOUND_FIRST:
                return new OptimisticBoundFirstNodeComparator(scoreBounderEnabled);
            default:
                throw new IllegalStateException("The nodeExplorationType ("
                        + this + ") is not implemented.");
        }
    }

}
