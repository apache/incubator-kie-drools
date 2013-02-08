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

package org.drools.planner.core.score.buildin.simple;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

public class SimpleScoreTest extends AbstractScoreTest {

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleScore.valueOf(-10),
                SimpleScore.valueOf(-10)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                SimpleScore.valueOf(-300),
                SimpleScore.valueOf(-20),
                SimpleScore.valueOf(-1),
                SimpleScore.valueOf(0),
                SimpleScore.valueOf(1)
        );
    }

}
