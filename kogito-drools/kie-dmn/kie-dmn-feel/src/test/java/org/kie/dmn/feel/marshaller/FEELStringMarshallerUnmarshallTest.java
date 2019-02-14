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
public class FEELStringMarshallerUnmarshallTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                // numbers
                { BuiltInType.NUMBER, "2", BigDecimal.valueOf( 2 ) },
                { BuiltInType.NUMBER, "2.0", BigDecimal.valueOf( 2.0 ) },
                { BuiltInType.NUMBER, "0.2", BigDecimal.valueOf( .2 ) },
                { BuiltInType.NUMBER, "0.2", BigDecimal.valueOf( 0.2 ) },
                { BuiltInType.NUMBER, "-0.2", BigDecimal.valueOf( -0.2 ) },
                // strings
                { BuiltInType.STRING, "foo", "foo" },
                { BuiltInType.STRING, "", "" },
                // booleans
                { BuiltInType.BOOLEAN, "true", true },
                { BuiltInType.BOOLEAN, "false", false },
                // dates
                { BuiltInType.DATE, "2017-07-01", LocalDate.of( 2017, 07, 01 ) },
                // time
                { BuiltInType.TIME, "14:32:55", LocalTime.of( 14, 32, 55 ) },
                { BuiltInType.TIME, "14:32:55.125-05:00", OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.ofHours( -5 ) ) },
                { BuiltInType.TIME, "14:32:55.125Z", OffsetTime.of( 14, 32, 55, 125000000, ZoneOffset.UTC ) },
                // date and time
                { BuiltInType.DATE_TIME, "2017-06-30T10:49:11", LocalDateTime.of( 2017, 06, 30, 10, 49, 11 ) },
                { BuiltInType.DATE_TIME, "2017-06-30T10:49:11.650", LocalDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000 ) },
                { BuiltInType.DATE_TIME, "2017-06-30T10:49:11.650+03:00", ZonedDateTime.of( 2017, 06, 30, 10, 49, 11, 650000000, ZoneOffset.ofHours( 3 ) ) },
                // days and time duration
                { BuiltInType.DURATION, "P5DT4H23M55S", Duration.ofDays( 5 ).plusHours( 4 ).plusMinutes( 23 ).plusSeconds( 55 ) },
                { BuiltInType.DURATION, "-P5DT4H23M55S", Duration.ofDays( -5 ).minusHours( 4 ).minusMinutes( 23 ).minusSeconds( 55 ) },
                { BuiltInType.DURATION, "P23D", Duration.ofDays( 23 ) },
                { BuiltInType.DURATION, "-P23D", Duration.ofDays( -23 ) },
                { BuiltInType.DURATION, "PT23H", Duration.ofHours( 23 ) },
                { BuiltInType.DURATION, "-PT23H", Duration.ofHours( -23 ) },
                { BuiltInType.DURATION, "PT23M", Duration.ofMinutes( 23 ) },
                { BuiltInType.DURATION, "-PT23M", Duration.ofMinutes( -23 ) },
                { BuiltInType.DURATION, "PT23S", Duration.ofSeconds( 23 ) },
                { BuiltInType.DURATION, "-PT23S", Duration.ofSeconds( -23 ) },
                { BuiltInType.DURATION, "PT0S", Duration.ofDays( 0 ) },
                { BuiltInType.DURATION, "P5DT4H", Duration.ofHours( 124 )},
                { BuiltInType.DURATION, "P737DT20H8M3S", Duration.ofSeconds( 63749283 )},
                // months and years duration
                { BuiltInType.DURATION, "P4Y5M", Period.of( 4, 5, 0 ) },
                { BuiltInType.DURATION, "P6Y1M", Period.of( 6, 1, 0 ) },
                { BuiltInType.DURATION, "-P6Y1M", Period.of( -6, -1, 0 ) },
                { BuiltInType.DURATION, "P0M", Period.of( 0, 0, 0 ) },
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
            assertThat( "Unmarshalling: '" + value + "'", FEELStringMarshaller.INSTANCE.unmarshall( feelType, value ), is( nullValue() ) );
        } else {
            assertThat( "Unmarshalling: '"+value+"'", FEELStringMarshaller.INSTANCE.unmarshall( feelType, value ), is( result ) );
        }
    }
}
