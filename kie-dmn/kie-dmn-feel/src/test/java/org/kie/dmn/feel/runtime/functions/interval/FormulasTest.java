/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions.interval;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// ScratchPad to support discussion on https://issues.omg.org/browse/DMN14-125
class FormulasTest {

    private static class R {
        public int start;
        public int end;
        public boolean start_included;
        public boolean end_included;

        public R(boolean start_included, int start, int end, boolean end_included) {
            this.start = start;
            this.end = end;
            this.start_included = start_included;
            this.end_included = end_included;
        }

    }

    static boolean not(boolean x) {
        return !x;
    }

    //    the ticket proposal has different formatting. static boolean overlapsBefore(R range1, R range2) {
    //        boolean formula = (range1.start < range2.start || (range1.start == range2.start && (range1.start_included && not(range2.start_included))))
    //        && (range1.end > range2.start || (range1.end == range2.start && range1.end_included && range2.start_included))
    //        && (range1.end < range2.end || (range1.end == range2.end && (not(range1.end_included) || range2.end_included)));
    //        return formula;
    //    }

    static boolean overlapsBefore(R range1, R range2) {
        boolean formula = 
                (range1.start < range2.start 
                        ||
                        (range1.start == range2.start 
                        &&
                        range1.start_included
                        &&
                        not(range2.start_included))) // change here.
                        && 
                        (range1.end > range2.start
                        ||
                        (range1.end == range2.start
                        && 
                        range1.end_included
                        && 
                        range2.start_included))
                        &&
                        (range1.end < range2.end
                        ||
                        (range1.end == range2.end
                        &&
                        (not(range1.end_included)
                        ||
                        range2.end_included )))
;
                return formula;
    }

    @Test
    void overlapsBeforeFormula() {
        assertThat(overlapsBefore(new R(true, 1, 5, true), new R(true, 3, 8, true))).isEqualTo(true);
        assertThat(overlapsBefore(new R(true, 1, 5, true), new R(true, 6, 8, true))).isEqualTo(false);
        assertThat(overlapsBefore(new R(true, 1, 5, true), new R(true, 5, 8, true))).isEqualTo(true);
        assertThat(overlapsBefore(new R(true, 1, 5, true), new R(false, 5, 8, true))).isEqualTo(false);
        assertThat(overlapsBefore(new R(true, 1, 5, false), new R(true, 5, 8, true))).isEqualTo(false);
        assertThat(overlapsBefore(new R(true, 1, 5, false), new R(false, 1, 5, true))).isEqualTo(true);
        assertThat(overlapsBefore(new R(true, 1, 5, true), new R(false, 1, 5, true))).isEqualTo(true);
        assertThat(overlapsBefore(new R(true, 1, 5, false), new R(true, 1, 5, true))).isEqualTo(false);
        assertThat(overlapsBefore(new R(true, 1, 5, true), new R(true, 1, 5, true))).isEqualTo(false);
    }

    static boolean overlaps(R range1, R range2) {
        boolean formula = 
                (range1.end > range2.start
                        ||
                        (range1.end == range2.start
                        &&
                        range1.end_included // remove (
                        &&                      // is is &&
                        range2.start_included)) // it is "start included" ,  remove )
                        &&
                        (range1.start < range2.end 
                        ||
                        (range1.start == range2.end 
                        &&
                        range1.start_included
                        &&
                        range2.end_included))
;
        return formula;
    }

    @Test
    void overlapsFormula() {
        assertThat(        overlaps( new R(true, 1, 5, true ), new R( true, 3, 8, true )  ) ).isEqualTo( true );   
        assertThat(        overlaps( new R(true, 3, 8, true ), new R( true, 1, 5, true )  ) ).isEqualTo( true );   
        assertThat(        overlaps( new R(true, 1, 8, true ), new R( true, 3, 5, true )  ) ).isEqualTo( true );   
        assertThat(        overlaps( new R(true, 3, 5, true ), new R( true, 1, 8, true )  ) ).isEqualTo( true );   
        assertThat(        overlaps( new R(true, 1, 5, true ), new R( true, 6, 8, true )  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(true, 6, 8, true ), new R( true, 1, 5, true )  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(true, 1, 5, true ), new R( true, 5, 8, true )  ) ).isEqualTo( true );   
        assertThat(        overlaps( new R(true, 1, 5, true ), new R( false,5, 8, true )  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(true, 1, 5, false), new R( true, 5, 8, true )  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(true, 1, 5, false), new R( false,5, 8, true )  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(true, 5, 8, true ), new R( true, 1, 5, true )  ) ).isEqualTo( true );   
        assertThat(        overlaps( new R(false,5, 8, true ), new R( true, 1, 5, true )  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(true, 5, 8, true ), new R( true, 1, 5, false)  ) ).isEqualTo( false);   
        assertThat(        overlaps( new R(false,5, 8, true ), new R( true, 1, 5, false)  ) ).isEqualTo( false);  

    }
}