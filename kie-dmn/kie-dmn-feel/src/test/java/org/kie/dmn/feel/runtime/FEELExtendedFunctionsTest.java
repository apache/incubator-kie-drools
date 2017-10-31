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

import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

public class FEELExtendedFunctionsTest
        extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "string(\"Happy %.0fth birthday, Mr %s!\", 38, \"Doe\")", "Happy 38th birthday, Mr Doe!", null},
                { "now()", ZonedDateTime.class , null},
                { "today()", LocalDate.class, null },
                { "modulo( 4, 3 )", new BigDecimal( "1" ), null },
        };
        return Arrays.asList( cases );
    }
}
