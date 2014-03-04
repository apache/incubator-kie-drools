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

package org.optaplanner.core.impl.score.buildin.simple;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;

import static org.junit.Assert.assertEquals;

public class SimpleScoreDefinitionTest {

    @Test
    public void testCalculateTimeGradient() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();

        assertEquals(0.0, scoreDefinition.calculateTimeGradient(
                SimpleScore.valueOf(0), SimpleScore.valueOf(10), SimpleScore.valueOf(0)), 0.0);
        assertEquals(0.6, scoreDefinition.calculateTimeGradient(
                SimpleScore.valueOf(0), SimpleScore.valueOf(10), SimpleScore.valueOf(6)), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                SimpleScore.valueOf(0), SimpleScore.valueOf(10), SimpleScore.valueOf(10)), 0.0);
        assertEquals(1.0, scoreDefinition.calculateTimeGradient(
                SimpleScore.valueOf(0), SimpleScore.valueOf(10), SimpleScore.valueOf(11)), 0.0);

        assertEquals(0.25, scoreDefinition.calculateTimeGradient(
                SimpleScore.valueOf(-10), SimpleScore.valueOf(30), SimpleScore.valueOf(0)), 0.0);
        assertEquals(0.33333, scoreDefinition.calculateTimeGradient(
                SimpleScore.valueOf(10), SimpleScore.valueOf(40), SimpleScore.valueOf(20)), 0.00001);
    }

}
