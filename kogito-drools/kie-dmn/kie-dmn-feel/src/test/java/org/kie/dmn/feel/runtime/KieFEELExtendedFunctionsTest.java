/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public class KieFEELExtendedFunctionsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "string(\"Happy %.0fth birthday, Mr %s!\", 38, \"Doe\")", "Happy 38th birthday, Mr Doe!", null},
                { "now()", ZonedDateTime.class , null},
                { "today()", LocalDate.class, null },
                { "modulo( 4, 3 )", new BigDecimal( "1" ), null },
                { "split( \"foo,bar,baz\", \",\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "split( delimiter: \",\", string: \"foo,bar,baz\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "split( \"foo;bar|baz\", \"[;|]\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "sqrt( 9 )", BigDecimal.valueOf( 3.0 ), null },
                { "sqrt( 10 )", new BigDecimal("3.162277660168379331998893544432719"), null },
                { "stddev( 10 )", null, FEELEvent.Severity.ERROR },
                { "nn sum( 10, null, 20, 40, null )", new BigDecimal("70", MathContext.DECIMAL128), null },
                { "stddev( 1, 2, 3 )", BigDecimal.valueOf( 1.0 ) , null},
                { "stddev( [1, 2, 3] )", BigDecimal.valueOf( 1.0 ) , null},
                { "after( 1, 2 )", Boolean.FALSE, null },
                { "after( date(\"2018-08-15\"), date(\"2018-07-25\") )", Boolean.TRUE, null },
                { "after( date(\"2018-08-15\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "after( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-07-25\") )", Boolean.TRUE, null },
                { "after( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-07-25\")..date(\"2018-07-31\")] )", Boolean.TRUE, null },
                { "before( date(\"2018-08-15\"), date(\"2018-07-25\") )", Boolean.FALSE, null },
                { "before( date(\"2018-08-15\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.FALSE, null },
                { "before( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-07-25\") )", Boolean.FALSE, null },
                { "before( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-07-25\")..date(\"2018-07-31\")] )", Boolean.FALSE, null },
                { "coincides( date(\"2018-08-15\"), date(\"2018-08-15\") )", Boolean.TRUE, null },
                { "coincides( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "starts( date(\"2018-07-25\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "starts( [date(\"2018-08-15\")..date(\"2018-08-20\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "started by( [date(\"2018-07-25\")..date(\"2018-08-31\")], date(\"2018-07-25\") )", Boolean.TRUE, null },
                { "started by( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-20\")] )", Boolean.TRUE, null },
                { "finishes( date(\"2018-08-10\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "finishes( [date(\"2018-08-25\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "finished by( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-08-31\") )", Boolean.TRUE, null },
                { "finished by( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-25\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "during( date(\"2018-07-29\"), [date(\"2018-07-25\")..date(\"2018-08-10\")] )", Boolean.TRUE, null },
                { "during( [date(\"2018-08-17\")..date(\"2018-08-20\")], [date(\"2018-08-15\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "includes( [date(\"2018-08-15\")..date(\"2018-08-31\")], date(\"2018-08-25\") )", Boolean.TRUE, null },
                { "includes( [date(\"2018-08-15\")..date(\"2018-08-31\")], [date(\"2018-08-20\")..date(\"2018-08-22\")] )", Boolean.TRUE, null },
                { "overlaps( [date(\"2018-08-15\")..date(\"2018-08-28\")], [date(\"2018-08-20\")..date(\"2018-08-31\")] )", Boolean.TRUE, null },
                { "overlapped by( [date(\"2018-08-20\")..date(\"2018-08-31\")], [date(\"2018-08-15\")..date(\"2018-08-22\")] )", Boolean.TRUE, null },
        };
        return addAdditionalParameters(cases, true);
    }
}
