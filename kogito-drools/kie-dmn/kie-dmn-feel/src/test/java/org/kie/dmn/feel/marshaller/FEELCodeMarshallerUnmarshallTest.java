package org.kie.dmn.feel.marshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
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
public class FEELCodeMarshallerUnmarshallTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
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
                { BuiltInType.UNKNOWN, "duration( \"P4Y5M\" )", Period.of( 4, 5, 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"P6Y1M\" )", Period.of( 6, 1, 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"-P6Y1M\" )", Period.of( -6, -1, 0 ) },
                { BuiltInType.UNKNOWN, "duration( \"P0M\" )", Period.of( 0, 0, 0 ) },
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

    @Parameterized.Parameter(0)
    public Type feelType;

    @Parameterized.Parameter(1)
    public String value;

    @Parameterized.Parameter(2)
    public Object result;

    @Test
    public void testExpression() {
        assertResult( feelType, value, result );
    }

    protected void assertResult(Type feelType, String value, Object result ) {
        if( result == null ) {
            assertThat( "Unmarshalling: '" + value + "'", FEELCodeMarshaller.INSTANCE.unmarshall( feelType, value ), is( nullValue() ) );
        } else {
            assertThat( "Unmarshalling: '"+value+"'", FEELCodeMarshaller.INSTANCE.unmarshall( feelType, value ), is( result ) );
        }
    }
}
