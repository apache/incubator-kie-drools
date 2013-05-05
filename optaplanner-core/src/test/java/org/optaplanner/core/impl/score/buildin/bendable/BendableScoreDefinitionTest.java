/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.bendable;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;

import static org.junit.Assert.*;

public class BendableScoreDefinitionTest {

    @Test
    public void testCalculateTimeGradient() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(1, 1);
        scoreDefinition.setRecursiveTimeGradientWeight(0.75);

        // Normal cases
        // Smack in the middle
        assertEquals(0.6, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-14}, new int[]{-340})), 0.0);
        // No hard broken, total soft broken
        assertEquals(0.75, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-10}, new int[]{-400})), 0.0);
        // Total hard broken, no soft broken
        assertEquals(0.25, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-20}, new int[]{-300})), 0.0);
        // No hard broken, more than total soft broken
        assertEquals(0.75, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-10}, new int[]{-900})), 0.0);
        // More than total hard broken, no soft broken
        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-90}, new int[]{-300})), 0.0);

        // Perfect min/max cases
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-10}, new int[]{-300}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-10}, new int[]{-300})), 0.0);
        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-20}, new int[]{-400})), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-10}, new int[]{-300})), 0.0);

        // Hard total delta is 0
        assertEquals(0.75 + (0.6 * 0.25), scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-10}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-10}, new int[]{-340})), 0.0);
        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-10}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-20}, new int[]{-340})), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-10}, new int[]{-400}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-0}, new int[]{-340})), 0.0);

        // Soft total delta is 0
        assertEquals((0.6 * 0.75) + 0.25, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-300}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-14}, new int[]{-300})), 0.0);
        assertEquals(0.6 * 0.75, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-300}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-14}, new int[]{-400})), 0.0);
        assertEquals((0.6 * 0.75) + 0.25, scoreDefinition.calculateTimeGradient(
                BendableScore.valueOf(new int[]{-20}, new int[]{-300}), BendableScore.valueOf(new int[]{-10}, new int[]{-300}),
                BendableScore.valueOf(new int[]{-14}, new int[]{-0})), 0.0);
    }

}
