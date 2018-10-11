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

package org.optaplanner.core.impl.exhaustivesearch.node.comparator;

import java.util.Comparator;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractNodeComparatorTest {

    protected ExhaustiveSearchNode buildNode(int depth, String score, long parentBreadth, long breadth) {
        return buildNode(depth,
                SimpleScore.parseScore(score),
                SimpleScore.parseScore(score).toInitializedScore(),
                parentBreadth, breadth);
    }

    protected ExhaustiveSearchNode buildNode(int depth, String score, int optimisticBound,
            long parentBreadth, long breadth) {
        return buildNode(depth,
                SimpleScore.parseScore(score),
                SimpleScore.of(optimisticBound),
                parentBreadth, breadth);
    }

    protected ExhaustiveSearchNode buildNode(int depth, SimpleScore score, SimpleScore optimisticBound,
            long parentBreadth, long breadth) {
        ExhaustiveSearchNode node = mock(ExhaustiveSearchNode.class);
        when(node.getDepth()).thenReturn(depth);
        when(node.getScore()).thenReturn(score);
        when(node.getOptimisticBound()).thenReturn(optimisticBound);
        when(node.getParentBreadth()).thenReturn(parentBreadth);
        when(node.getBreadth()).thenReturn(breadth);
        when(node.toString()).thenReturn(score.toString());
        return node;
    }

    protected static void assertLesser(Comparator<ExhaustiveSearchNode> comparator,
            ExhaustiveSearchNode a, ExhaustiveSearchNode b) {
        assertTrue("Node (" + a + ") must be lesser than node (" + b + ").", comparator.compare(a, b) < 0);
        assertTrue("Node (" + b + ") must be greater than node (" + a + ").", comparator.compare(b, a) > 0);
    }

    protected static void assertScoreCompareToOrder(Comparator<ExhaustiveSearchNode> comparator,
            ExhaustiveSearchNode... nodes) {
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                assertLesser(comparator, nodes[i], nodes[j]);
            }
        }
    }

}
