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

import static org.junit.Assert.*;

public class SimpleScoreTest extends AbstractScoreTest {

    @Test
    public void compareTo() {
        assertScoreOrder(
                DefaultSimpleScore.valueOf(-300),
                DefaultSimpleScore.valueOf(-20),
                DefaultSimpleScore.valueOf(-1),
                DefaultSimpleScore.valueOf(0),
                DefaultSimpleScore.valueOf(1)
        );
    }

}
