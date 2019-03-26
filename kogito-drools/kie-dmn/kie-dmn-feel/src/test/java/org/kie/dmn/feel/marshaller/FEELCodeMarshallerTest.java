package org.kie.dmn.feel.marshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.math.BigDecimal;
import java.time.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class FEELCodeMarshallerTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // numbers
                { BigDecimal.valueOf( 2 ), "2" },
                { BigDecimal.valueOf( 2.0 ), "2.0" },
                { BigDecimal.valueOf( .2 ), "0.2" },
                { BigDecimal.valueOf( 0.2 ), "0.2" },
                { BigDecimal.valueOf( -0.2 ), "-0.2" },
                // strings
                { "foo", "\"foo\"" },
                { "", "\"\"" },
                // booleans
                { true, "true" },
                { false, "false" },
                // dates
                { LocalDate.of( 2017, 07, 01 ), "date( \"2017-07-01\" )" },
                // time
                { LocalTime.of( 14, 32, 55 ), "time( \"14:32:55\" )" },
                { OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.ofHours( -5 ) ), "time( \"14:32:55.125-05:00\" )" },
                { OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.UTC ), "time( \"14:32:55.125Z\" )" },
                // date and time
                { LocalDateTime.of( 2017, 06, 30, 10, 49, 11 ), "date and time( \"2017-06-30T10:49:11\" )" },
                { LocalDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000 ), "date and time( \"2017-06-30T10:49:11.65\" )" },
                { OffsetDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000, ZoneOffset.ofHours( 3 ) ), "date and time( \"2017-06-30T10:49:11.65+03:00\" )" },
                // days and time duration
                { Duration.ofDays( 5 ).plusHours( 4 ).plusMinutes( 23 ).plusSeconds( 55 ), "duration( \"P5DT4H23M55S\" )" },
                { Duration.ofDays( -5 ).minusHours( 4 ).minusMinutes( 23 ).minusSeconds( 55 ), "duration( \"-P5DT4H23M55S\" )" },
                { Duration.ofDays( 23 ), "duration( \"P23D\" )" },
                { Duration.ofDays( -23 ), "duration( \"-P23D\" )" },
                { Duration.ofHours( 23 ), "duration( \"PT23H\" )" },
                { Duration.ofHours( -23 ), "duration( \"-PT23H\" )" },
                { Duration.ofMinutes( 23 ), "duration( \"PT23M\" )" },
                { Duration.ofMinutes( -23 ), "duration( \"-PT23M\" )" },
                { Duration.ofSeconds( 23 ), "duration( \"PT23S\" )" },
                { Duration.ofSeconds( -23 ), "duration( \"-PT23S\" )" },
                { Duration.ofDays( 0 ), "duration( \"PT0S\" )" },
                { Duration.ofHours( 124 ), "duration( \"P5DT4H\" )"},
                { Duration.ofSeconds( 63749283 ), "duration( \"P737DT20H8M3S\" )"},
                // months and years duration
                { Period.of( 4, 5, 12 ), "duration( \"P4Y5M\" )" },
                { Period.of( 4, 25, 0 ), "duration( \"P6Y1M\" )" },
                { Period.of( -4, -25, 0 ), "duration( \"-P6Y1M\" )" },
                { Period.of( 0, 0, -4 ), "duration( \"P0M\" )" },
                { Period.of( 0, 0, 0 ), "duration( \"P0M\" )" },
                // lists
                { Arrays.asList( 1, 2, 3, 4 ), "[ 1, 2, 3, 4 ]" },
                { Arrays.asList( "foo", "bar", "baz" ), "[ \"foo\", \"bar\", \"baz\" ]" },
                { Arrays.asList( Duration.ofDays( 4 ), Duration.ofDays( 2 ), Duration.ofHours( 25 ) ), "[ duration( \"P4D\" ), duration( \"P2D\" ), duration( \"P1DT1H\" ) ]" },
                { Arrays.asList( Arrays.asList( 1, 2 ), Arrays.asList( 3, 4 ) ), "[ [ 1, 2 ], [ 3, 4 ] ]" },
                // ranges
                { new RangeImpl( Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN ), "[ \"a\" .. \"z\" )" },
                { new RangeImpl( Range.RangeBoundary.CLOSED, Duration.ofHours( 30 ), Duration.ofHours( 50 ), Range.RangeBoundary.OPEN ), "[ duration( \"P1DT6H\" ) .. duration( \"P2DT2H\" ) )" },
                // context
                { new LinkedHashMap() {{ put( "Full Name", "John Doe"); put( "Age", 35 ); put( "Date of Birth", LocalDate.of( 1982, 6, 9 ) ); }},
                  "{ Full Name : \"John Doe\", Age : 35, Date of Birth : date( \"1982-06-09\" ) }" },
                // null
                { null, "null" }
        };
        return Arrays.asList( cases );
    }

    @Parameterized.Parameter(0)
    public Object value;

    @Parameterized.Parameter(1)
    public String result;

    @Test
    public void testExpression() {
        assertResult( value, result );
    }

    protected void assertResult( Object value, String result ) {
        if( result == null ) {
            assertThat( "Marshalling: '" + value + "'", FEELCodeMarshaller.INSTANCE.marshall( value ), is( nullValue() ) );
        } else {
            assertThat( "Marshalling: '"+value+"'", FEELCodeMarshaller.INSTANCE.marshall( value ), is( result ) );
        }
    }
}
