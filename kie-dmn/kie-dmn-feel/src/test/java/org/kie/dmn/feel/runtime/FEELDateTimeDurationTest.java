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
package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.custom.ZoneTime;

import static org.kie.dmn.feel.runtime.functions.TimeFunction.timeStringWithSeconds;

public class FEELDateTimeDurationTest extends BaseFEELTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Object result, FEELEvent.Severity severity, FEEL_TARGET testFEELTarget, Boolean useExtendedProfile, FEELDialect feelDialect) {
        expression( expression,  result, severity, testFEELTarget, useExtendedProfile, feelDialect);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // date/time/duration function invocations
                { "date(\"2016-07-29\")", DateTimeFormatter.ISO_DATE.parse( "2016-07-29", LocalDate::from ) , null},
                { "@\"2016-07-29\"", DateTimeFormatter.ISO_DATE.parse( "2016-07-29", LocalDate::from ) , null},
                { "date(\"-0105-07-29\")", DateTimeFormatter.ISO_DATE.parse( "-0105-07-29", LocalDate::from ) , null}, // 105 BC
                {"date(\"2016-15-29\")", null , FEELEvent.Severity.ERROR },
                { "date(\"2016-12-48\")", null , FEELEvent.Severity.ERROR },
                { "date( 10 )", null , FEELEvent.Severity.ERROR },
                { "date( 2016, 8, 2 )", LocalDate.of( 2016, 8, 2 ) , null},
                { "date( -0105, 8, 2 )", LocalDate.of( -105, 8, 2 ) , null},
                { "date( 2016, 15, 2 )", null , FEELEvent.Severity.ERROR },
                { "date( 2016, 12, 48 )", null , FEELEvent.Severity.ERROR },
                { "date( date and time(\"2016-07-29T05:48:23.765-05:00\") )", LocalDate.of( 2016, 7, 29 ) , null},
                { "date( date and time(\"2016-07-29T05:48:23Z\") )", LocalDate.of( 2016, 7, 29 ) , null},
                { "time(\"23:59:00\")", DateTimeFormatter.ISO_TIME.parse( "23:59:00", LocalTime::from ) , null},
                { "@\"23:59:00\"", DateTimeFormatter.ISO_TIME.parse( "23:59:00", LocalTime::from ) , null},
                { "time(\"05:48:23.765\")", DateTimeFormatter.ISO_TIME.parse( "05:48:23.765", LocalTime::from ) , null},
                { "time(\"23:59:00z\")", DateTimeFormatter.ISO_TIME.parse( "23:59:00z", OffsetTime::from ) , null},
                { "time(\"13:20:00-05:00\")", DateTimeFormatter.ISO_TIME.parse( "13:20:00-05:00", OffsetTime::from ) , null},
                { "time( 14, 52, 25, null )", LocalTime.of( 14, 52, 25 ) , null},
                { "time( 14, 52, 25, duration(\"PT5H\"))", OffsetTime.of( 14, 52, 25, 0, ZoneOffset.ofHours( 5 ) ) , null},
                { "time( date and time(\"2016-07-29T05:48:23\") )", LocalTime.of( 5, 48, 23, 0 ) , null},
                { "time( date and time(\"2016-07-29T05:48:23Z\") )", OffsetTime.of( 5, 48, 23, 0, ZoneOffset.UTC ) , null},
                { "time( date and time(\"2016-07-29T05:48:23.765-05:00\") )", OffsetTime.of( 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 ) ) , null},
                { "date and time(\"2016-07-29T05:48:23\")", LocalDateTime.of( 2016, 7, 29, 5, 48, 23, 0 ) , null},
                { "@\"2016-07-29T05:48:23\"", LocalDateTime.of( 2016, 7, 29, 5, 48, 23, 0 ) , null},
                { "date and time( 2016, 7, 29, 5, 48, 23 )", LocalDateTime.of( 2016, 7, 29, 5, 48, 23, 0 ) , null},
                { "date and time(\"2016-07-29T05:48:23Z\")", ZonedDateTime.of(2016, 7, 29, 5, 48, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "date and time( 2016, 7, 29, 5, 48, 23, -5 )", OffsetDateTime.of(2016, 7, 29, 5, 48, 23, 0, ZoneOffset.ofHours( -5 ) ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\")", DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) , null},
                { "date and time(date(\"2016-07-29\"), time(\"05:48:23.765-05:00\") )", DateTimeFormatter.ISO_DATE_TIME.parse( "2016-07-29T05:48:23.765-05:00", ZonedDateTime::from ) , null},
                { "duration( \"P2DT20H14M\" )", Duration.parse( "P2DT20H14M" ) , null},
                { "@\"P2DT20H14M\"", Duration.parse( "P2DT20H14M" ) , null},
                { "duration( \"P2Y2M\" )", ComparablePeriod.parse( "P2Y2M" ) , null},
                { "@\"P2Y2M\"", ComparablePeriod.parse( "P2Y2M" ) , null},
                { "duration( \"P26M\" )", ComparablePeriod.parse( "P2Y2M" ) , null},
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") )", ComparablePeriod.parse( "P1Y8M" ) , null},
                { "@\"xyz\"", null , FEELEvent.Severity.ERROR },
                { "(@\"13:20:00@Europe/Rome\").timezone", "Europe/Rome" , null},
                { "(@\"13:20:00@Etc/UTC\").timezone", "Etc/UTC" , null},
                { "(@\"13:20:00@Etc/GMT\").timezone", "Etc/GMT" , null},
                { "-duration( \"P2Y2M\" )", ComparablePeriod.parse( "-P2Y2M" ) , null},
                {"@\"2023-10-10T10:31:00@Australia/Melbourne\"", DateTimeFormatter.ISO_DATE_TIME.parse("2023-10-10T10" +
                                                                                                               ":31+11:00[Australia/Melbourne]", ZonedDateTime::from), null},
                {"@\"10:15:00@Australia/Melbourne\"", ZoneTime.of(LocalTime.of(10, 15), ZoneId.of("Australia/Melbourne"), true), null},

                // comparison operators
                { "duration( \"P1Y6M\" ) = duration( \"P1Y6M\" )", Boolean.TRUE , null},
                { "duration( \"P1Y6M\" ) = duration( \"P1Y8M\" )", Boolean.FALSE , null},
                { "duration( \"P1Y\" ) = duration( \"P1Y\" )", Boolean.TRUE , null},
                { "duration( \"P1Y\" ) = duration( \"P1M\" )", Boolean.FALSE , null},
                { "duration( \"P1Y6M\" ) <= duration( \"P1Y8M\" )", Boolean.TRUE , null},
                { "duration( \"P1Y6M\" ) < duration( \"P1Y8M\" )", Boolean.TRUE , null},
                { "duration( \"P1Y6M\" ) > duration( \"P1Y8M\" )", Boolean.FALSE , null},
                { "duration( \"P1Y6M\" ) >= duration( \"P1Y8M\" )", Boolean.FALSE , null},
                { "duration( \"P1Y6M\" ) = null", Boolean.FALSE , null},
                { "duration( \"P1Y6M\" ) != null", Boolean.TRUE , null},
                { "duration( \"P1Y6M\" ) > null", null , null},
                { "duration( \"P1Y6M\" ) < null", null , null},
                { "-duration( \"P1Y6M\" ) = (duration( \"P1Y6M\" )*-1) ", Boolean.TRUE , null},
                { "-duration( \"P1Y8M\" ) < duration( \"P1Y6M\" )", Boolean.TRUE , null},
                { "-duration( \"P1Y8M\" ) < -duration( \"P1Y6M\" )", Boolean.TRUE , null},
                { "-duration( \"P1Y6M\" ) > -duration( \"P1Y8M\" )", Boolean.TRUE , null},
                {"time(\"10:30:00@Europe/Rome\") = time(\"10:30:00@Europe/Rome\")", Boolean.TRUE, null},
                {"time(\"10:30:00@Europe/Rome\") = time(\"10:30:00@Europe/Paris\")", Boolean.TRUE, null},
                {"is(time(\"10:30:00@Europe/Rome\"), time(\"10:30:00@Europe/Paris\"))", Boolean.FALSE, null},
                {"time(\"10:30:00\") = time(\"10:30:00@Europe/Paris\")", Boolean.TRUE, null},
                {"is(time(\"10:30:00\"), time(\"10:30:00@Europe/Paris\"))", Boolean.FALSE, null},
                {"time(\"10:30:00\") = time(\"10:30:00+00:00\")", Boolean.TRUE, null},
                {"is(time(\"10:30:00\"), time(\"10:30:00+00:00\"))", Boolean.FALSE, null},
                { "date and time(\"2018-12-08T00:00:00\") = date and time(\"2018-12-08T00:00:00\")", Boolean.TRUE , null},
                { "date and time(\"2018-12-08T00:00:00@Europe/Rome\") = date and time(\"2018-12-08T00:00:00@Europe/Rome\")", Boolean.TRUE , null},
                { "date and time(\"2018-12-08T00:00:00@Europe/Rome\") = date and time(\"2018-12-08T00:00:00@Europe/Paris\")", Boolean.TRUE , null},
                { "is(date and time(\"2018-12-08T00:00:00@Europe/Rome\"), date and time(\"2018-12-08T00:00:00@Europe/Paris\"))", Boolean.FALSE , null},
                { "date and time(\"2018-12-08T00:00:00\") = date and time(\"2018-12-08T00:00:00@Europe/Rome\")", Boolean.TRUE , null},
                { "is(date and time(\"2018-12-08T00:00:00\"), date and time(\"2018-12-08T00:00:00@Europe/Rome\"))", Boolean.FALSE , null},
                {"time(\"10:30:00@Europe/Rome\") <= time(\"10:30:00@Europe/Rome\")", Boolean.TRUE, null},
                {"time(\"10:30:00@Europe/Rome\") <= time(\"10:30:00@Europe/Paris\")", Boolean.TRUE, null},
                {"is(time(\"10:30:00@Europe/Rome\"), time(\"10:30:00@Europe/Paris\"))", Boolean.FALSE, null},
                {"time(\"10:30:00\") <= time(\"10:30:00@Europe/Paris\")", Boolean.TRUE, null},
                {"is(time(\"10:30:00\"), time(\"10:30:00@Europe/Paris\"))", Boolean.FALSE, null},
                {"time(\"10:30:00\") <= time(\"10:30:00+00:00\")", Boolean.TRUE, null},
                {"is(time(\"10:30:00\"), time(\"10:30:00+00:00\"))", Boolean.FALSE, null},
                { "date and time(\"2018-12-08T00:00:00\") <= date and time(\"2018-12-08T00:00:00\")", Boolean.TRUE , null},
                { "date and time(\"2018-12-08T00:00:00@Europe/Rome\") <= date and time(\"2018-12-08T00:00:00@Europe/Rome\")", Boolean.TRUE , null},
                { "date and time(\"2018-12-08T00:00:00@Europe/Rome\") <= date and time(\"2018-12-08T00:00:00@Europe/Paris\")", Boolean.TRUE , null},
                { "is(date and time(\"2018-12-08T00:00:00@Europe/Rome\"), date and time(\"2018-12-08T00:00:00@Europe/Paris\"))", Boolean.FALSE , null},
                { "date and time(\"2018-12-08T00:00:00\") <= date and time(\"2018-12-08T00:00:00@Europe/Rome\")", Boolean.TRUE , null},
                { "is(date and time(\"2018-12-08T00:00:00\"), date and time(\"2018-12-08T00:00:00@Europe/Rome\"))", Boolean.FALSE , null},
                {"time(\"10:30:00@Europe/Rome\") < time(\"10:30:00@Europe/Rome\")", Boolean.FALSE, null},
                {"time(\"10:30:00@Europe/Rome\") < time(\"10:30:00@Europe/Paris\")", Boolean.FALSE, null},
                {"time(\"10:30:00\") < time(\"10:30:00@Europe/Paris\")", Boolean.FALSE, null},
                {"time(\"10:30:00\") < time(\"10:30:00+00:00\")", Boolean.FALSE, null},
                { "date and time(\"2018-12-08T00:00:00\") < date and time(\"2018-12-08T00:00:00\")", Boolean.FALSE , null},
                { "date and time(\"2018-12-08T00:00:00@Europe/Rome\") < date and time(\"2018-12-08T00:00:00@Europe/Rome\")", Boolean.FALSE , null},
                { "date and time(\"2018-12-08T00:00:00@Europe/Rome\") < date and time(\"2018-12-08T00:00:00@Europe/Paris\")", Boolean.FALSE , null},
                { "date and time(\"2018-12-08T00:00:00\") < date and time(\"2018-12-08T00:00:00@Europe/Rome\")", Boolean.FALSE , null},
                { "date and time(\"2018-12-01T08:00:00\") <= date and time(\"2018-12-01T09:30:00@Europe/Rome\")", Boolean.TRUE , null},
                { "date and time(\"2018-12-01T16:00:00\") >= date and time(\"2018-12-01T09:30:00@Europe/Rome\")", Boolean.TRUE , null},
                { "date and time(\"2018-12-01T09:30:00@Europe/Rome\") between date and time(\"2018-12-01T08:00:00\") and date and time(\"2018-12-01T16:00:00\")", Boolean.TRUE , null},

                // Math operations with date, time, duration
                { "date and time(\"2021-01-01T10:10:10\") + date( \"2021-01-01\" )", null , FEELEvent.Severity.ERROR},
                { "date(\"2021-01-01\") + date and time(\"2021-01-01T10:10:10\")", null , FEELEvent.Severity.ERROR},
                { "date(\"2021-01-01\") + date(\"2021-01-02\")", null , FEELEvent.Severity.ERROR},
                { "@\"2021-01-02\" - @\"2021-01-01T10:10:10\"", null, FEELEvent.Severity.ERROR},
                { "@\"2021-01-02T10:10:10@Europe/Paris\" - @\"2021-01-01\"", Duration.parse("P1DT9H10M10S") , null},
                { "@\"2021-01-02T10:10:10+01:00\" - @\"2021-01-01\"", Duration.parse("P1DT9H10M10S") , null},
                { "@\"2021-01-02\" - @\"2021-01-01T10:10:10+11:00\"", Duration.parse("P1DT49M50S") , null},
                // either both have timezone or they both don't have timezone
                { "@\"2021-01-02T10:10:10\" - @\"2021-01-01T10:10:10+11:00\"", null, FEELEvent.Severity.ERROR},
                { "@\"2021-01-01T10:10:10+11:00\" - @\"2021-01-02T10:10:10\"", null, FEELEvent.Severity.ERROR},
                { "@\"2021-01-01T10:10:10\" - @\"2021-01-02T10:10:10@Europe/Paris\"", null , FEELEvent.Severity.ERROR},
                { "@\"2021-01-02T10:10:10@Europe/Paris\" - @\"2021-01-01T10:10:10\"", null , FEELEvent.Severity.ERROR},
                { "@\"2021-01-02\" - @\"1995-01-01\"", Duration.parse("P9498D") , null},
                { "time(\"23:00:00\") - time(\"21:00:00\")", Duration.parse("PT2H") , null},
                { "time(\"23:00:00\") - @\"2021-01-02T10:10:10\"", null, FEELEvent.Severity.ERROR},
                { "time(\"23:00:00\") - @\"2021-01-02T10:10:10@Europe/Paris\"", null, FEELEvent.Severity.ERROR},
                { "@\"2021-01-02T10:10:10\" - time(\"23:00:00\")", null, FEELEvent.Severity.ERROR},
                { "@\"2021-01-02T10:10:10@Europe/Paris\" - time(\"23:00:00\")", null, FEELEvent.Severity.ERROR},
                { "duration( \"P2Y2M\" ) + duration( \"P1Y1M\" )", ComparablePeriod.parse("P3Y3M"), null },
                { "duration( \"P2DT20H14M\" ) + duration( \"P1DT1H1M\" )", Duration.parse( "P3DT21H15M" ) , null},
                { "date and time(\"2016-07-29T05:48:23Z\") + duration( \"P1Y1M\" )", ZonedDateTime.of(2017, 8, 29, 5, 48, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "date and time(\"2016-07-29T05:48:23\") + duration( \"P1Y1M\" )", LocalDateTime.of(2017, 8, 29, 5, 48, 23, 0) , null},
                { "date and time(\"2016-07-29T05:48:23Z\") + duration( \"P1DT1H1M\" )", ZonedDateTime.of(2016, 7, 30, 6, 49, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "date and time(\"2016-07-29T05:48:23\") + duration( \"P1DT1H1M\" )", LocalDateTime.of(2016, 7, 30, 6, 49, 23, 0) , null},
                { "date(\"2016-07-29\") + duration( \"P3D\" )", LocalDate.of(2016, 8, 1) , null},
                { "date(\"2016-07-29\") + duration( \"P1Y1M\" )", LocalDate.of(2017, 8, 29) , null},
                { "duration( \"P3D\" ) + date(\"2016-07-29\")", LocalDate.of(2016, 8, 1) , null},
                { "duration( \"P1Y1M\" ) + date(\"2016-07-29\")", LocalDate.of(2017, 8, 29) , null},
                { "duration( \"P1Y1M\" ) + date and time(\"2016-07-29T05:48:23Z\")", ZonedDateTime.of(2017, 8, 29, 5, 48, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "duration( \"P1Y1M\" ) + date and time(\"2016-07-29T05:48:23\")", LocalDateTime.of(2017, 8, 29, 5, 48, 23, 0) , null},
                { "duration( \"P1DT1H1M\" ) + date and time(\"2016-07-29T05:48:23Z\")", ZonedDateTime.of(2016, 7, 30, 6, 49, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "duration( \"P1DT1H1M\" ) + date and time(\"2016-07-29T05:48:23\")", LocalDateTime.of(2016, 7, 30, 6, 49, 23, 0) , null},
                { "-duration( \"P1DT1H1M\" ) + date and time(\"2016-07-29T05:48:23\")", LocalDateTime.of(2016, 7, 28, 4, 47, 23, 0) , null},
                { "time(\"22:57:00\") + duration( \"PT1H1M\" )", LocalTime.of(23, 58, 0) , null},
                { "duration( \"PT1H1M\" ) + time(\"22:57:00\")", LocalTime.of(23, 58, 0) , null},
                { "-duration( \"PT1H1M\" ) + time(\"22:57:00\")", LocalTime.of(21, 56, 0) , null},
                { "time( 22, 57, 00, duration(\"PT5H\")) + duration( \"PT1H1M\" )", OffsetTime.of( 23, 58, 0, 0, ZoneOffset.ofHours( 5 ) ) , null},
                { "duration( \"PT1H1M\" ) + time( 22, 57, 00, duration(\"PT5H\"))", OffsetTime.of( 23, 58, 0, 0, ZoneOffset.ofHours( 5 ) ) , null},

                { "@\"P1D\" + @\"2023-10-10T10:31:00@Australia/Melbourne\"", DateTimeFormatter.ISO_DATE_TIME.parse("2023-10-11T10" +
                                                                                                                             ":31+11:00[Australia/Melbourne]", ZonedDateTime::from), null},
                { "@\"-P1D\" + @\"2023-10-10T10:31:00@Australia/Melbourne\"", DateTimeFormatter.ISO_DATE_TIME.parse("2023-10-09T10" +
                                                                                                                             ":31+11:00[Australia/Melbourne]", ZonedDateTime::from), null},
                { "@\"P1D\" + @\"10:15:00@Australia/Melbourne\"", getCorrectZoneTime("10:15", "Australia/Melbourne"), null},
                { "@\"-P1D\" + @\"10:15:00@Australia/Melbourne\"", getCorrectZoneTime("10:15", "Australia/Melbourne"), null},
                { "@\"PT1H\" + @\"10:15:00@Australia/Melbourne\"", getCorrectZoneTime("11:15", "Australia/Melbourne"), null},
                { "@\"-PT1H\" + @\"10:15:00@Australia/Melbourne\"", getCorrectZoneTime("09:15", "Australia/Melbourne"), null},


                { "@\"10:15:00@Australia/Melbourne\" + @\"P1D\"", getCorrectZoneTime("10:15", "Australia/Melbourne"), null},
                { "@\"10:15:00@Australia/Melbourne\" - @\"P1D\"", getCorrectZoneTime("10:15", "Australia/Melbourne"), null},
                { "@\"10:15:00@Australia/Melbourne\" + @\"-P1D\"", getCorrectZoneTime("10:15", "Australia/Melbourne"), null},
                { "@\"10:15:00@Australia/Melbourne\" + @\"PT1H\"", getCorrectZoneTime("11:15", "Australia/Melbourne"), null},
                { "@\"10:15:00@Australia/Melbourne\" - @\"PT1H\"", getCorrectZoneTime("09:15", "Australia/Melbourne"), null},
                { "@\"10:15:00@Australia/Melbourne\" + @\"-PT1H\"", getCorrectZoneTime("09:15", "Australia/Melbourne"), null},

                {"string(@\"10:10@Australia/Melbourne\" + @\"PT1H\")", "11:10@Australia/Melbourne", null},
                {"string(@\"10:10:00@Australia/Melbourne\" + @\"PT1H\")", "11:10:00@Australia/Melbourne", null},


                // TODO support for zones - fix when timezones solved out (currently returns ZonedDateTime)
//                { "date and time(\"2016-07-29T05:48:23.765-05:00\") + duration( \"P1Y1M\" ) ", OffsetDateTime.of(2017, 8, 29, 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 )), null},
//                { "date and time(\"2016-07-29T05:48:23.765-05:00\") + duration( \"P1DT1H1M\" ) ", OffsetDateTime.of(2016, 7, 30, 6, 49, 23, 765000000, ZoneOffset.ofHours( -5 )), null},
//                { "duration( \"P1Y1M\" ) + date and time(\"2016-07-29T05:48:23.765-05:00\")", OffsetDateTime.of(2017, 8, 29, 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 )), null},
//                { "duration( \"P1DT1H1M\" ) + date and time(\"2016-07-29T05:48:23.765-05:00\")", OffsetDateTime.of(2016, 7, 30, 6, 49, 23, 765000000, ZoneOffset.ofHours( -5 )), null},
//                { "date and time(\"2016-07-29T05:48:23.765-05:00\") - duration( \"P1Y1M\" ) ", OffsetDateTime.of(2015, 6, 29, 5, 48, 23, 765000000, ZoneOffset.ofHours( -5 )), null},
//                { "date and time(\"2016-07-29T05:48:23.765-05:00\") - duration( \"P1DT1H1M\" ) ", OffsetDateTime.of(2016, 7, 28, 4, 47, 23, 765000000, ZoneOffset.ofHours( -5 )), null},

                { "duration( \"P2Y2M\" ) - duration( \"P1Y1M\" )", ComparablePeriod.parse("P1Y1M"), null },
                { "duration( \"P2DT20H14M\" ) - duration( \"P1DT1H1M\" )", Duration.parse( "P1DT19H13M" ) , null},
                { "date and time(\"2016-07-29T05:48:23Z\") - duration( \"P1Y1M\" )", ZonedDateTime.of(2015, 6, 29, 5, 48, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "date and time(\"2016-07-29T05:48:23\") - duration( \"P1Y1M\" )", LocalDateTime.of(2015, 6, 29, 5, 48, 23, 0) , null},
                { "date and time(\"2016-07-29T05:48:23Z\") - duration( \"P1DT1H1M\" )", ZonedDateTime.of(2016, 7, 28, 4, 47, 23, 0, ZoneId.of("Z").normalized()) , null},
                { "date and time(\"2016-07-29T05:48:23\") - duration( \"P1DT1H1M\" )", LocalDateTime.of(2016, 7, 28, 4, 47, 23, 0) , null},
                { "date(\"2016-07-29\") - duration( \"P1D\" )", LocalDate.of(2016, 7, 28) , null},
                { "date(\"2016-07-29\") - duration( \"P1Y1M\" )", LocalDate.of(2015, 6, 29) , null},
                { "date(\"2016-07-29\") + (-duration( \"P1Y1M\" ))", LocalDate.of(2015, 6, 29) , null},
                { "time(\"22:57:00\") - duration( \"PT1H1M\" )", LocalTime.of(21, 56, 0) , null},
                { "time( 22, 57, 00, duration(\"PT5H\")) - duration( \"PT1H1M\" )", OffsetTime.of( 21, 56, 0, 0, ZoneOffset.ofHours( 5 ) ) , null},

                { "duration( \"P2Y2M\" ) - (-duration( \"P1Y1M\" ))", ComparablePeriod.parse("P3Y3M"), null },
                { "-duration( \"P2Y2M\" ) + duration( \"P1Y1M\" )", ComparablePeriod.parse("-P1Y1M"), null },
                { "duration( \"P2Y2M\" ) * 2", ComparablePeriod.parse("P52M"), null },
                { "2 * duration( \"P2Y2M\" )", ComparablePeriod.parse("P52M"), null },
                { "-duration( \"P2Y2M\" ) * 2", ComparablePeriod.parse("-P52M"), null },
                { "2 * -duration( \"P2Y2M\" )", ComparablePeriod.parse("-P52M"), null },
                { "duration( \"P2Y2M\" ) * duration( \"P2Y2M\" )", null , FEELEvent.Severity.ERROR},
                { "duration( \"P2DT20H14M\" ) * 2", Duration.parse( "P4DT40H28M" ) , null},
                { "2 * duration( \"P2DT20H14M\" )", Duration.parse( "P4DT40H28M" ) , null},
                { "duration( \"P2DT20H14M\" ) * duration( \"P2DT20H14M\" )", null , FEELEvent.Severity.ERROR},
                { "@\"PT1H\" * @\"PT25H\"", null, FEELEvent.Severity.ERROR  },
                { "duration( \"P4Y4M\" ) * 2.5", ComparablePeriod.parse("P10Y10M"), null },
                { "duration( \"P4DT1H\" ) * 1.5", Duration.parse("P6DT1H30M"), null },
                { "duration( \"PT23H\" ) * 2.5", Duration.parse("P2DT9H30M"), null },

                { "duration( \"P2Y2M\" ) / 2", ComparablePeriod.parse("P13M"), null },
                { "2 / duration( \"P2Y2M\" )", null, FEELEvent.Severity.ERROR },
                { "duration( \"P2Y2M\" ) / duration( \"P2Y2M\" )", BigDecimal.valueOf(1), null },
                { "duration( \"P2DT20H14M\" ) / 2", Duration.parse( "P1DT10H7M" ) , null},
                { "2 / duration( \"P2DT20H14M\" )", null, FEELEvent.Severity.ERROR},
                { "duration( \"P2DT20H14M\" ) / duration( \"P2DT20H14M\" )", BigDecimal.valueOf(1) , null},
                { "duration( \"P10Y11M\" ) / 2.5", ComparablePeriod.parse("P4Y4M"), null },
                { "duration( \"P10Y11M\" ) / -2.5", ComparablePeriod.parse("-P4Y4M"), null },
                { "duration( \"P10Y11M\" ) / 0", null, FEELEvent.Severity.ERROR },
                { "duration( \"P10DT10H10M\" ) / 2.5", Duration.parse("P4DT4H4M"), null },

                {"time(\"10:30:00\") instance of time", Boolean.TRUE, null},
                {"time(\"10:30:00\") instance of date", Boolean.FALSE, null},
                {"time(\"10:30:00@Europe/Rome\") instance of time", Boolean.TRUE, null},
                {"time(\"10:30:00@Europe/Rome\") instance of date", Boolean.FALSE, null},
                {"time(\"10:30:00z\") instance of time", Boolean.TRUE, null},
                {"time(\"10:30:00z\") instance of date", Boolean.FALSE, null},

                { "date( 2016, 8, 2 ).year", BigDecimal.valueOf( 2016 ) , null},
                { "date( 2016, 8, 2 ).month", BigDecimal.valueOf( 8 ) , null},
                { "date( 2016, 8, 2 ).day", BigDecimal.valueOf( 2 ) , null},
                { "date( 2017, 11, 8 ).weekday", BigDecimal.valueOf( 3 ), null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").year", BigDecimal.valueOf( 2016 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").month", BigDecimal.valueOf( 7 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").day", BigDecimal.valueOf( 29 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").weekday", BigDecimal.valueOf( 5 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").hour", BigDecimal.valueOf( 5 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").minute", BigDecimal.valueOf( 48 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").second", BigDecimal.valueOf( 23 ) , null},
                { "date and time(\"2016-07-29T05:48:23.765-05:00\").time offset", Duration.parse( "PT-5H" ), null},
                { "date and time(\"2018-12-10T10:30:00@Europe/Rome\").timezone", "Europe/Rome", null},
                { "date and time(\"2018-12-10T10:30:00@Etc/UTC\").timezone", "Etc/UTC", null},
                { "time(\"13:20:00-05:00\").hour", BigDecimal.valueOf( 13 ), null},
                { "time(\"13:20:00-05:00\").minute", BigDecimal.valueOf( 20 ), null},
                { "time(\"13:20:00-05:00\").second", BigDecimal.valueOf( 0 ), null},
                { "time(\"13:20:00-05:00\").time offset", Duration.parse( "PT-5H" ), null},
                { "time(\"13:20:00@Europe/Rome\").timezone", "Europe/Rome" , null},
                { "time(\"13:20:00@Etc/UTC\").timezone", "Etc/UTC" , null},
                { "time(\"13:20:00@Etc/GMT\").timezone", "Etc/GMT" , null},
                { "duration( \"P2DT20H14M\" ).days", BigDecimal.valueOf(2) , null},
                { "duration( \"P2DT20H14M\" ).hours", BigDecimal.valueOf(20) , null},
                { "duration( \"P2DT20H14M\" ).minutes", BigDecimal.valueOf(14) , null},
                { "duration( \"P2DT20H14M5S\" ).seconds", BigDecimal.valueOf(5) , null},
                { "duration(\"P1Y\").years", BigDecimal.valueOf(1) , null},
                { "duration(\"P1Y\").months", BigDecimal.valueOf(0) , null},
                { "duration(\"P1Y\").days", null , null},
                { "duration(\"P1Y\").hours", null , null},
                { "duration(\"P1Y\").minutes", null , null},
                { "duration(\"P1Y\").seconds", null , null},
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") ).years", BigDecimal.valueOf(1) , null},
                { "years and months duration( date(\"2011-12-22\"), date(\"2013-08-24\") ).months", BigDecimal.valueOf(8) , null},
                { "date and time(\"2017-05-14\")", LocalDateTime.of( 2017, 5, 14, 0, 0, 0, 0 ) , null},
                { "date(\"2017-05-12\")-date(\"2017-04-25\")", Duration.ofDays( 17 ) , null},
                { "time(date(\"2017-08-10\"))", DateTimeFormatter.ISO_TIME.parse( "00:00:00z", OffsetTime::from ) , null },
                { "duration(\"P1Y\") instance of years and months duration", Boolean.TRUE , null },
                { "duration(\"P1Y\") instance of days and time duration", Boolean.FALSE , null },
                { "duration(\"P1D\") instance of years and months duration", Boolean.FALSE , null },
                { "duration(\"P1D\") instance of days and time duration", Boolean.TRUE , null },

                { "date and time(\"2017-11-10T11:00@Europe/Paris\") < date and time(\"2017-11-10T10:00Z\")", Boolean.FALSE , null},
                { "date and time(\"2017-11-10T11:00@Europe/Paris\") > date and time(\"2017-11-10T10:00Z\")", Boolean.FALSE , null},
                { "date and time(\"2017-11-10T11:00@Europe/Paris\") = date and time(\"2017-11-10T10:00Z\")", Boolean.TRUE , null},
                { "is(date and time(\"2017-11-10T11:00@Europe/Paris\"), date and time(\"2017-11-10T10:00Z\"))", Boolean.FALSE , null},
                { "is(date(\"2012-12-25\"), time(\"23:00:50\"))", Boolean.FALSE , null},
                { "is(date(\"2012-12-25\"), date(\"2012-12-25\"))", Boolean.TRUE , null},
                { "is(time(\"23:00:50z\"), time(\"23:00:50\"))", Boolean.FALSE , null},
                { "is(time(\"23:00:50z\"), time(\"23:00:50@Etc/UTC\"))", Boolean.FALSE , null},
                { "is(time(\"23:00:50\"), time(\"23:00:50@Etc/UTC\"))", Boolean.FALSE , null},
                { "time(\"23:00:50z\") = time(\"23:00:50\")", Boolean.TRUE , null},
                { "time(\"23:00:50z\") = time(\"23:00:50@Etc/UTC\")", Boolean.TRUE , null},
                { "time(\"23:00:50\")  = time(\"23:00:50@Etc/UTC\")", Boolean.TRUE , null},

                {"time(\"10:30:00\").time offset", null, null},
                {"date and time(\"2018-12-10T10:30:00\").time offset", null, null},
                {"date( 2020, 2, 31 )", null, FEELEvent.Severity.ERROR},
                {"date( \"2020-02-31\" )", null, FEELEvent.Severity.ERROR},
                {"duration(\"-PT10H\")", Duration.parse("-PT10H"), null},
                {"@\"-PT10H\"", Duration.parse("-PT10H"), null},

                { "@\"2021-01-02\" - @\"PT1H\"", LocalDate.of(2021, 1, 1 ), null  },
                { "@\"2021-01-01\" - @\"-PT25H\"", LocalDate.of(2021, 1, 2 ), null  },
                { "@\"2021-01-01\" + @\"PT1H\"", LocalDate.of(2021, 1, 1 ), null  },
                { "@\"2020-12-30\" + @\"-PT25H\"", LocalDate.of(2020, 12, 28 ), null  }
        };
        return addAdditionalParameters(cases, false);
    }

    private static ZoneTime getCorrectZoneTime(String baseTime, String zone) {
        LocalTime localTime = DateTimeFormatter.ISO_TIME.parse(baseTime, LocalTime::from );
        ZoneId zoneId = ZoneId.of(zone);
        return ZoneTime.of(localTime, zoneId, timeStringWithSeconds(baseTime));
    }

}
