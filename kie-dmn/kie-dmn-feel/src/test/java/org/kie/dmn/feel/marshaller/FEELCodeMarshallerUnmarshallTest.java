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
package org.kie.dmn.feel.marshaller;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class FEELCodeMarshallerUnmarshallTest {

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // numbers
                { BuiltInType.UNKNOWN, "2", BigDecimal.valueOf( 2 ) },
                { BuiltInType.UNKNOWN, "2.0", BigDecimal.valueOf( 2.0 ) },
                { BuiltInType.UNKNOWN, "0.2", BigDecimal.valueOf( .2 ) },
                { BuiltInType.UNKNOWN, "0.2", BigDecimal.valueOf( 0.2 ) },
                { BuiltInType.UNKNOWN, "-0.2", BigDecimal.valueOf( -0.2 ) },
                // strings
                { BuiltInType.UNKNOWN, "\"foo\"", "foo" },
                { BuiltInType.UNKNOWN, "\"\"", "" },
                // booleans
                { BuiltInType.UNKNOWN, "true", true },
                { BuiltInType.UNKNOWN, "false", false },
                // dates
                { BuiltInType.UNKNOWN, "date( \"2017-07-01\" )", LocalDate.of( 2017, 07, 01 ) },
                // time
                { BuiltInType.UNKNOWN, "time( \"14:32:55\" )", LocalTime.of( 14, 32, 55 ) },
                { BuiltInType.UNKNOWN, "time( \"14:32:55.125-05:00\" )", OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.ofHours( -5 ) ) },
                { BuiltInType.UNKNOWN, "time( \"14:32:55.125Z\" )", OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.UTC ) },
                // date and time
                { BuiltInType.UNKNOWN, "date and time( \"2017-06-30T10:49:11\" )", LocalDateTime.of( 2017, 06, 30, 10, 49, 11 ) },
                { BuiltInType.UNKNOWN, "date and time( \"2017-06-30T10:49:11.650\" )", LocalDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000 ) },
                { BuiltInType.UNKNOWN, "date and time( \"2017-06-30T10:49:11.650+03:00\" )", ZonedDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000, ZoneOffset.ofHours( 3 ) ) },
                // days and time duration
                { BuiltInType.UNKNOWN, "duration( \"P5DT4H23M55S\" )", Duration.ofDays( 5 ).plusHours( 4 ).plusMinutes( 23 ).plusSeconds( 55 ) },
                { BuiltInType.UNKNOWN, "duration( \"-P5DT4H23M55S\" )", Duration.ofDays( -5 ).minusHours( 4 ).minusMinutes( 23 ).minusSeconds( 55 ) },
                { BuiltInType.UNKNOWN, "duration( \"P23D\" )", Duration.ofDays( 23 ) },
                { BuiltInType.UNKNOWN, "duration( \"-P23D\" )", Duration.ofDays( -23 ) },
                { BuiltInType.UNKNOWN, "duration( \"PT23H\" )", Duration.ofHours( 23 ) },
                { BuiltInType.UNKNOWN, "duration( \"-PT23H\" )", Duration.ofHours( -23 ) },
                { BuiltInType.UNKNOWN, "duration( \"PT23M\" )", Duration.ofMinutes( 23 ) },
                { BuiltInType.UNKNOWN, "duration( \"-PT23M\" )", Duration.ofMinutes( -23 ) },
                { BuiltInType.UNKNOWN, "duration( \"PT23S\" )", Duration.ofSeconds( 23 ) },
                { BuiltInType.UNKNOWN, "duration( \"-PT23S\" )", Duration.ofSeconds( -23 ) },
                { BuiltInType.UNKNOWN, "duration( \"PT0S\" )", Duration.ofDays( 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"P5DT4H\" )", Duration.ofHours( 124 )},
                { BuiltInType.UNKNOWN, "duration( \"P737DT20H8M3S\" )", Duration.ofSeconds( 63749283 )},
                // months and years duration
                { BuiltInType.UNKNOWN, "duration( \"P4Y5M\" )", ComparablePeriod.of( 4, 5, 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"P6Y1M\" )", ComparablePeriod.of( 6, 1, 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"-P6Y1M\" )", ComparablePeriod.of( -6, -1, 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"P0M\" )", ComparablePeriod.of( 0, 0, 0 ) },
                // lists
                { BuiltInType.UNKNOWN, "[ 1, 2, 3, 4 ]", Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ), BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) },
                { BuiltInType.UNKNOWN, "[ \"foo\", \"bar\", \"baz\" ]", Arrays.asList( "foo", "bar", "baz" ) },
                { BuiltInType.UNKNOWN, "[ duration( \"P4D\" ), duration( \"P2D\" ), duration( \"P1DT1H\" ) ]", Arrays.asList( Duration.ofDays( 4 ), Duration.ofDays( 2 ), Duration.ofHours( 25 ) ) },
                { BuiltInType.UNKNOWN, "[ [ 1, 2 ], [ 3, 4 ] ]", Arrays.asList( Arrays.asList( BigDecimal.valueOf( 1 ), BigDecimal.valueOf( 2 ) ), Arrays.asList( BigDecimal.valueOf( 3 ), BigDecimal.valueOf( 4 ) ) ) },
                // ranges
                { BuiltInType.UNKNOWN, "[ \"a\" .. \"z\" )", new RangeImpl( Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN ) },
                { BuiltInType.UNKNOWN, "[ duration( \"P1DT6H\" ) .. duration( \"P2DT2H\" ) )", new RangeImpl( Range.RangeBoundary.CLOSED, Duration.ofHours( 30 ), Duration.ofHours( 50 ), Range.RangeBoundary.OPEN ) },
                // context
                { BuiltInType.UNKNOWN, "{ Full Name : \"John Doe\", Age : 35, Date of Birth : date( \"1982-06-09\" ) }",
                  new LinkedHashMap() {{ put( "Full Name", "John Doe"); put( "Age", BigDecimal.valueOf( 35 ) ); put( "Date of Birth", LocalDate.of( 1982, 6, 9 ) ); }} },
                // null
                { BuiltInType.UNKNOWN, "null", null }
        };
        return Arrays.asList( cases );
    }
    public Type feelType;
    public String value;
    public Object result;

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0} ({1}) = {2}")
    public void expression(Type feelType, String value, Object result) {
        initFEELCodeMarshallerUnmarshallTest(feelType, value, result);
        assertResult( feelType, value, result );
    }

    protected void assertResult(Type feelType, String value, Object result ) {
        if( result == null ) {
        	assertThat(FEELCodeMarshaller.INSTANCE.unmarshall( feelType, value )).as("Unmarshalling: '" + value + "'").isNull();
        } else {
        	assertThat(FEELCodeMarshaller.INSTANCE.unmarshall( feelType, value )).as("Unmarshalling: '" + value + "'").isEqualTo(result);
        }
    }

    public void initFEELCodeMarshallerUnmarshallTest(Type feelType, String value, Object result) {
        this.feelType = feelType;
        this.value = value;
        this.result = result;
    }
}
