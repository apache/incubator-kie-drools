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
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import static org.assertj.core.api.Assertions.assertThat;

public class FEELStringMarshallerTest {

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // numbers
                { BigDecimal.valueOf( 2 ), "2" },
                { BigDecimal.valueOf( 2.0 ), "2.0" },
                { BigDecimal.valueOf( .2 ), "0.2" },
                { BigDecimal.valueOf( 0.2 ), "0.2" },
                { BigDecimal.valueOf( -0.2 ), "-0.2" },
                // strings
                { "foo", "foo" },
                { "", "" },
                // booleans
                { true, "true" },
                { false, "false" },
                // dates
                { LocalDate.of( 2017, 07, 01 ), "2017-07-01" },
                // time
                { LocalTime.of( 14, 32, 55 ), "14:32:55" },
                { OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.ofHours( -5 ) ), "14:32:55.125-05:00" },
                { OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.UTC ), "14:32:55.125Z" },
                // date and time
                { LocalDateTime.of( 2017, 06, 30, 10, 49, 11 ), "2017-06-30T10:49:11" },
                { LocalDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000 ), "2017-06-30T10:49:11.65" },
                { OffsetDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000, ZoneOffset.ofHours( 3 ) ), "2017-06-30T10:49:11.65+03:00" },
                // days and time duration
                { Duration.ofDays( 5 ).plusHours( 4 ).plusMinutes( 23 ).plusSeconds( 55 ), "P5DT4H23M55S" },
                { Duration.ofDays( -5 ).minusHours( 4 ).minusMinutes( 23 ).minusSeconds( 55 ), "-P5DT4H23M55S" },
                { Duration.ofDays( 23 ), "P23D" },
                { Duration.ofDays( -23 ), "-P23D" },
                { Duration.ofHours( 23 ), "PT23H" },
                { Duration.ofHours( -23 ), "-PT23H" },
                { Duration.ofMinutes( 23 ), "PT23M" },
                { Duration.ofMinutes( -23 ), "-PT23M" },
                { Duration.ofSeconds( 23 ), "PT23S" },
                { Duration.ofSeconds( -23 ), "-PT23S" },
                { Duration.ofDays( 0 ), "PT0S" },
                { Duration.ofHours( 124 ), "P5DT4H"},
                { Duration.ofSeconds( 63749283 ), "P737DT20H8M3S"},
                // months and years duration
                { Period.of( 4, 5, 12 ), "P4Y5M" },
                { Period.of( 4, 25, 0 ), "P6Y1M" },
                { Period.of( -4, -25, 0 ), "-P6Y1M" },
                { Period.of( 0, 0, -4 ), "P0M" },
                { Period.of( 0, 0, 0 ), "P0M" },
                { ComparablePeriod.of( 4, 5, 12 ), "P4Y5M" },
                { ComparablePeriod.of( 4, 25, 0 ), "P6Y1M" },
                { ComparablePeriod.of( -4, -25, 0 ), "-P6Y1M" },
                { ComparablePeriod.of( 0, 0, -4 ), "P0M" },
                { ComparablePeriod.of( 0, 0, 0 ), "P0M" },
                // lists
                {Arrays.asList( null, null ), "[ null, null ]"},
                { Arrays.asList( 1, 2, 3, 4 ), "[ 1, 2, 3, 4 ]" },
                { Arrays.asList( "foo", "bar", "baz" ), "[ foo, bar, baz ]" },
                { Arrays.asList( Duration.ofDays( 4 ), Duration.ofDays( 2 ), Duration.ofHours( 25 ) ), "[ P4D, P2D, P1DT1H ]" },
                { Arrays.asList( Arrays.asList( 1, 2 ), Arrays.asList( 3, 4 ) ), "[ [ 1, 2 ], [ 3, 4 ] ]" },
                // ranges
                { new RangeImpl( Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN ), "[ a .. z )" },
                { new RangeImpl( Range.RangeBoundary.CLOSED, Duration.ofHours( 30 ), Duration.ofHours( 50 ), Range.RangeBoundary.OPEN ), "[ P1DT6H .. P2DT2H )" },
                // context
                { new LinkedHashMap() {{ put( "Full Name", "John Doe"); put( "Age", 35 ); put( "Date of Birth", LocalDate.of( 1982, 6, 9 ) ); }},
                  "{ Full Name : John Doe, Age : 35, Date of Birth : 1982-06-09 }" },
                // null
                { null, "null" }
        };
        return Arrays.asList( cases );
    }
    public Object value;
    public String result;

    @MethodSource("data")
    @ParameterizedTest(name = "{index}: {0} ({1}) = {2}")
    public void expression(Object value, String result) {
        initFEELStringMarshallerTest(value, result);
        assertResult( value, result );
    }

    protected void assertResult( Object value, String result ) {
        if( result == null ) {
        	assertThat(FEELStringMarshaller.INSTANCE.marshall(value)).as("Marshalling: '" + value + "'").isNull();
        } else {
        	assertThat(FEELStringMarshaller.INSTANCE.marshall(value)).as("Marshalling: '" + value + "'").isEqualTo(result);
        }
    }

    public void initFEELStringMarshallerTest(Object value, String result) {
        this.value = value;
        this.result = result;
    }
}
