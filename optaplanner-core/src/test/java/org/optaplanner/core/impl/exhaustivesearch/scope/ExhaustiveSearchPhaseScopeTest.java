/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.exhaustivesearch.scope;

import java.util.TreeSet;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.AbstractNodeComparatorTest;
import org.optaplanner.core.impl.exhaustivesearch.node.comparator.ScoreFirstNodeComparator;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.junit.Assert.*;

public class ExhaustiveSearchPhaseScopeTest extends AbstractNodeComparatorTest {

    @Test
    public void testNodePruning() {
        ExhaustiveSearchPhaseScope<TestdataSolution> phase
                = new ExhaustiveSearchPhaseScope<>(new DefaultSolverScope<>());
        phase.setExpandableNodeQueue(new TreeSet<>(new ScoreFirstNodeComparator(true)));
        phase.addExpandableNode(buildNode(0, "0", 0, 0));
        phase.addExpandableNode(buildNode(0, "1", 0, 0));
        phase.addExpandableNode(buildNode(0, "2", 0, 0));
        phase.setBestPessimisticBound(SimpleScore.of(Integer.MIN_VALUE));
        phase.registerPessimisticBound(SimpleScore.of(1));
        assertEquals(1, phase.getExpandableNodeQueue().size());
    }

}
