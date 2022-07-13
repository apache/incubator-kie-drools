/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.rule;

import org.drools.core.rule.FixedDuration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedDurationTest {
    @Test
    public void testConstruct() throws Exception {
        FixedDuration dur = new FixedDuration();

        assertThat(dur.getDuration(null)).isEqualTo(0);

        dur = new FixedDuration( 42000 );

        assertThat(dur.getDuration(null)).isEqualTo(42000);
    }

    @Test
    public void testAdd() throws Exception {
        final FixedDuration dur = new FixedDuration();

        assertThat(dur.getDuration(null)).isEqualTo(0);

        dur.addSeconds( 42 );

        assertThat(dur.getDuration(null)).isEqualTo(42000);

        dur.addMinutes( 2 );

        assertThat(dur.getDuration(null)).isEqualTo(162000);

        dur.addHours( 2 );

        assertThat(dur.getDuration(null)).isEqualTo(7362000);

        dur.addDays( 2 );

        assertThat(dur.getDuration(null)).isEqualTo(180162000);

        dur.addWeeks( 2 );

        assertThat(dur.getDuration(null)).isEqualTo(1389762000);
    }
}
