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

import static org.junit.Assert.*;

public class BendableScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(2, new BendableScoreDefinition(1, 1).getLevelsSize());
        assertEquals(7, new BendableScoreDefinition(3, 4).getLevelsSize());
        assertEquals(7, new BendableScoreDefinition(4, 3).getLevelsSize());
        assertEquals(5, new BendableScoreDefinition(0, 5).getLevelsSize());
        assertEquals(5, new BendableScoreDefinition(5, 0).getLevelsSize());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new BendableScoreDefinition(1, 1).getFeasibleLevelsSize());
        assertEquals(3, new BendableScoreDefinition(3, 4).getFeasibleLevelsSize());
        assertEquals(4, new BendableScoreDefinition(4, 3).getFeasibleLevelsSize());
        assertEquals(0, new BendableScoreDefinition(0, 5).getFeasibleLevelsSize());
        assertEquals(5, new BendableScoreDefinition(5, 0).getFeasibleLevelsSize());
    }

}
