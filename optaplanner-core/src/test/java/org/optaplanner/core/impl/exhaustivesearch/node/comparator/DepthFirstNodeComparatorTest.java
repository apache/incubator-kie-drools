/*
 * Copyright 2014 JBoss Inc
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

import org.junit.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class DepthFirstNodeComparatorTest extends AbstractNodeComparatorTest {

    @Test
    public void compare() {
        DepthFirstNodeComparator comparator = new DepthFirstNodeComparator(true);
        assertScoreCompareToOrder(comparator,
                buildNode(1, -110, 5),
                buildNode(1, -110, 7),
                buildNode(1, -90, 5),
                buildNode(1, -90, 7),
                buildNode(2, -110, 5),
                buildNode(2, -110, 7),
                buildNode(2, -90, 5),
                buildNode(2, -90, 7));
    }

}
