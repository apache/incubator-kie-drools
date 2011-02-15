/**
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.score.definition;

import org.drools.planner.core.score.DefaultHardAndSoftScore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoffrey De Smet
 */
public class HardAndSoftScoreDefinitionTest {

    @Test
    public void testCalculateTimeGradient() {
        HardAndSoftScoreDefinition scoreDefinition = new HardAndSoftScoreDefinition();
        scoreDefinition.setHardScoreTimeGradientWeight(0.75);

        // Normal cases
        // Smack in the middle
        assertEquals(0.6, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-14,-340)), 0.0);
        // No hard broken, total soft broken
        assertEquals(0.75, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-10,-400)), 0.0);
        // Total hard broken, no soft broken
        assertEquals(0.25, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-20,-300)), 0.0);
        // No hard broken, more than total soft broken
        assertEquals(0.75, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-10,-900)), 0.0);
        // More than total hard broken, no soft broken
        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-90,-300)), 0.0);

        // Perfect min/max cases
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-10,-300), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-10,-300)), 0.0);
        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-20,-400)), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-10,-300)), 0.0);

        // Hard total delta is 0
        assertEquals(0.6, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-10,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-10,-340)), 0.0);
        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-10,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-20,-340)), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-10,-400), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-0,-340)), 0.0);

        // Soft total delta is 0
        assertEquals((0.6 * 0.75) + 0.25, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-300), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-14,-300)), 0.0);
        assertEquals(0.6 * 0.75, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-300), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-14,-400)), 0.0);
        assertEquals((0.6 * 0.75) + 0.25, scoreDefinition.calculateTimeGradient(
                DefaultHardAndSoftScore.valueOf(-20,-300), DefaultHardAndSoftScore.valueOf(-10,-300),
                DefaultHardAndSoftScore.valueOf(-14,-0)), 0.0);
    }

}
