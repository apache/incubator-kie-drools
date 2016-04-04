/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.comparator;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FlatteningHardSoftScoreComparatorTest {

    private int expectedResult;
    private int modifier;
    private String firstScore;
    private String secondScore;

    public FlatteningHardSoftScoreComparatorTest(int expectedResult, int modifier, String firstScore, String secondScore) {
        this.expectedResult = expectedResult;
        this.modifier = modifier;
        this.firstScore = firstScore;
        this.secondScore = secondScore;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection parameters() {
        String simpleScore = "10hard/123soft";
        String lowHardScore = "10hard/987654321soft";
        String highHardScore = "987654321hard/123soft";
        return Arrays.asList(new Object[][]{
                {0, 0, simpleScore, simpleScore}, // 0 - comparison according to soft score
                {0, 1, simpleScore, simpleScore}, // 1 - no changes
                {0, 1024, simpleScore, simpleScore}, // "huge" modifier
                {-1, 0, simpleScore, lowHardScore},
                {-1, 1, simpleScore, lowHardScore},
                {-1, 1024, simpleScore, lowHardScore},
                {1, 0, lowHardScore, simpleScore},
                {1, 1, lowHardScore, simpleScore},
                {1, 1024, lowHardScore, simpleScore},
                {1, 0, lowHardScore, highHardScore},
                {-1, 1, lowHardScore, highHardScore},
                {-1, 1024, lowHardScore, highHardScore},
                {-1, 0, highHardScore, lowHardScore},
                {1, 1, highHardScore, lowHardScore},
                {1, 1024, highHardScore, lowHardScore},
                {0, 0, highHardScore, simpleScore},
                {1, 1, highHardScore, simpleScore},
                {1, 1024, highHardScore, simpleScore}
        });
    }

    @Test
    public void compare() {
        assertEquals(expectedResult, new FlatteningHardSoftScoreComparator(modifier)
                .compare(new HardSoftScoreDefinition().parseScore(firstScore),
                        new HardSoftScoreDefinition().parseScore(secondScore)));
    }
}
