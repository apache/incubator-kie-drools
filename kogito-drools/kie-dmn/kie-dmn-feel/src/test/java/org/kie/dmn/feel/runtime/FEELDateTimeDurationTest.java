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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import org.junit.runners.Parameterized;

public class FEELDateTimeDurationTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // date/time/duration function invocations
                { "date(\"2016-07-29\")", DateTimeFormatter.ISO_DATE.parse( "2016-07-29", LocalDate::from ) },
                { "date(\"-0105-07-29\")", DateTimeFormatter.ISO_DATE.parse( "-0105-07-29", LocalDate::from ) }, // 105 BC
                { "date(\"2016-15-29\")", null },
                { "date(\"2016-12-48\")", null },
                { "date( 10 )", null },
                { "date( 2016, 8, 2 )", LocalDate.of( 2016, 8, 2 ) },
                { "date( -0105, 8, 2 )", LocalDate.of( -105, 8, 2 ) },
                { "date( 2016, 15, 2 )", null },
                { "date( 2016, 12, 48 )", null },
                { "date( date and time(\"2016-07-29T05:48:23.765-05:00\") )", LocalDate.of( 2016, 7, 29 ) },
                { "date( date and time(\"2016-07-29T05:48:23Z\") )", LocalDate.of( 2016, 7, 29 ) },
                { "time(\"23:59:00\")", DateTimeFormatter.ISO_TIME.parse( "23:59:00", LocalTime::from ) },
                { "time(\"05:48:23.765\")", DateTimeFormatter.ISO_TIME.parse( "05:48:23.765", LocalTime::from ) },
                { "time(\"23:59:00z\")", DateTimeFormatter.ISO_TIME.parse( "23:59:00z", OffsetTime::from ) },
                { "time(\"13:20:00-05:00\")", DateTimeFormatter.ISO_TIME.parse( "13:20:00-05:00", OffsetTime::from ) },
                { "time( 14, 52, 25, null )", LocalTime.of( 14, 52, 25 ) },
                { "time( 14, 52, 25, duration(\"PT5H\"))", OffsetTime.of( 14, 52, 25, 0, ZoneOffset.ofHours( 5 ) ) },
                { "time( date and time(\"2016-07-29T05:48:23\") )", LocalTime.of( 5, 48, 23, 0 ) },
                { "time( date and time(\"2016-07-29T05:48:23Z\") )", OffsetTime.of( 5, 48, 23, 0, ZoneOffset.UTC ) },
                { "time( date and time(\"2016-07-29T05:48:23.765-05:00\") )", OffsetTime.of( 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 ) ) },
                { "date and time(\"2016-07-29T05:48:23\")", LocalDateTime.of( 2016, 7, 29, 5, 48, 23, 0 ) },
                { "date and time(\"2016-07-29T05:48:23Z\")", ZonedDateTime.of(2016, 7, 29, 5, 48, 23, 0, ZoneId.of("Z").normalized()) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\")", DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) },
                { "date and time(date(\"2016-07-29\"), time(\"05:48:23.765-05:00\") )", DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) },
                { "duration( \"P2DT20H14M\" )", Duration.parse( "P2DT20H14M" ) },
                { "duration( \"P2Y2M\" )", Period.parse( "P2Y2M" ) },
                { "duration( \"P26M\" )", Period.parse( "P26M" ) },
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") )", Period.parse( "P1Y8M" ) },

                // comparison operators
                { "duration( \"P1Y6M\" ) = duration( \"P1Y6M\" )", Boolean.TRUE },
                { "duration( \"P1Y6M\" ) = duration( \"P1Y8M\" )", Boolean.FALSE },
                { "duration( \"P1Y\" ) = duration( \"P1Y\" )", Boolean.TRUE },
                { "duration( \"P1Y\" ) = duration( \"P1M\" )", Boolean.FALSE },
                { "duration( \"P1Y6M\" ) <= duration( \"P1Y8M\" )", Boolean.TRUE },
                { "duration( \"P1Y6M\" ) < duration( \"P1Y8M\" )", Boolean.TRUE },
                { "duration( \"P1Y6M\" ) > duration( \"P1Y8M\" )", Boolean.FALSE },
                { "duration( \"P1Y6M\" ) >= duration( \"P1Y8M\" )", Boolean.FALSE },
                { "duration( \"P1Y6M\" ) = null", Boolean.FALSE },
                { "duration( \"P1Y6M\" ) != null", Boolean.TRUE },
                { "duration( \"P1Y6M\" ) > null", null },
                { "duration( \"P1Y6M\" ) < null", null },

                { "date( 2016, 8, 2 ).year", BigDecimal.valueOf( 2016 ) },
                { "date( 2016, 8, 2 ).month", BigDecimal.valueOf( 8 ) },
                { "date( 2016, 8, 2 ).day", BigDecimal.valueOf( 2 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").year", BigDecimal.valueOf( 2016 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").month", BigDecimal.valueOf( 7 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").day", BigDecimal.valueOf( 29 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").hour", BigDecimal.valueOf( 5 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").minute", BigDecimal.valueOf( 48 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").second", BigDecimal.valueOf( 23 ) },
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").time offset", Duration.parse( "PT-5H" )},
                // TODO When we have timezones solved out, this test case should be modified and uncommented
                // { "date and time(\"2016-07-29T05:48:23.765@SomeTimeZoneFormat\").timezone", someTimezoneResult},
                { "time(\"13:20:00-05:00\").hour", BigDecimal.valueOf( 13 )},
                { "time(\"13:20:00-05:00\").minute", BigDecimal.valueOf( 20 )},
                { "time(\"13:20:00-05:00\").second", BigDecimal.valueOf( 0 )},
                { "time(\"13:20:00-05:00\").time offset", Duration.parse( "PT-5H" )},
                // TODO When we have timezones solved out, this test case should be modified and uncommented
//                { "time(\"13:20:00@SomeTimeZoneFormat\").timezone", someTimeZoneResult },
                { "duration( \"P2DT20H14M\" ).days", BigDecimal.valueOf(2) },
                { "duration( \"P2DT20H14M\" ).hours", BigDecimal.valueOf(20) },
                { "duration( \"P2DT20H14M\" ).minutes", BigDecimal.valueOf(14) },
                { "duration( \"P2DT20H14M5S\" ).seconds", BigDecimal.valueOf(5) },
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") ).years", BigDecimal.valueOf(1) },
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") ).months", BigDecimal.valueOf(8) },
                { "date and time(\"2017-05-14\")", LocalDateTime.of( 2017, 5, 14, 0, 0, 0, 0 ) },
                { "date(\"2017-05-12\")-date(\"2017-04-25\")", Duration.ofDays( 17 ) }

        };
        return Arrays.asList( cases );
    }
}
