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

package org.drools.planner.core.score.buildin.simplelong;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

public class SimpleLongScoreTest extends AbstractScoreTest {

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleLongScore.valueOf(-10L),
                SimpleLongScore.valueOf(-10L)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                SimpleLongScore.valueOf(((long) Integer.MIN_VALUE) - 4000L),
                SimpleLongScore.valueOf(-300L),
                SimpleLongScore.valueOf(-20L),
                SimpleLongScore.valueOf(-1L),
                SimpleLongScore.valueOf(0L),
                SimpleLongScore.valueOf(1L),
                SimpleLongScore.valueOf(((long) Integer.MAX_VALUE) + 4000L)
        );
    }

}
