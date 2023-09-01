package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.runners.Parameterized;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

public class FEEL12ExtendedFunctionsTest extends BaseFEELTest {

    @Parameterized.Parameters(name = "{index}: {0} ({1}) = {2}")
    public static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
                { "modulo( 4, 3 )", new BigDecimal( "1" ), null },
                { "modulo(  12,  5 )", new BigDecimal(  "2" ), null },
                { "modulo( -12,  5 )", new BigDecimal(  "3" ), null },
                { "modulo(  12, -5 )", new BigDecimal( "-3" ), null },
                { "modulo( -12, -5 )", new BigDecimal( "-2" ), null },
                { "modulo(  10.1,  4.5 )", new BigDecimal(  "1.1" ), null },
                { "modulo( -10.1,  4.5 )", new BigDecimal(  "3.4" ), null },
                { "modulo(  10.1, -4.5 )", new BigDecimal( "-3.4" ), null },
                { "modulo( -10.1, -4.5 )", new BigDecimal( "-1.1" ), null },
                { "split( \"foo,bar,baz\", \",\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "split( delimiter: \",\", string: \"foo,bar,baz\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "split( \"foo;bar|baz\", \"[;|]\" )", Arrays.asList( "foo", "bar", "baz" ), null },
                { "sqrt( 9 )", BigDecimal.valueOf( 3.0 ), null },
                { "sqrt( 10 )", new BigDecimal("3.162277660168379331998893544432719"), null },
                { "stddev( 10 )", null, FEELEvent.Severity.ERROR },
                { "stddev( 1, 2, 3 )", BigDecimal.valueOf( 1.0 ) , null},
                { "stddev( [1, 2, 3] )", BigDecimal.valueOf( 1.0 ) , null},
                { "stddev( 2, 4, 7, 5 )", new BigDecimal("2.081665999466132735282297706979931") , null},
                { "stddev( [ 47 ] )", null, FEELEvent.Severity.ERROR },
                { "stddev( 47 )", null, FEELEvent.Severity.ERROR },
                { "stddev( [ ] )", null, FEELEvent.Severity.ERROR },
                {"mode( 6, 3, 9, 6, 6 )", List.of(BigDecimal.valueOf(6)), null },
                { "mode( [6, 1, 9, 6, 1] )",  Arrays.asList(BigDecimal.valueOf( 1 ),  BigDecimal.valueOf( 6 ) ), null },
                {"mode( [ ] )", List.of(), null },
        };
        return addAdditionalParameters(cases, false);
    }
}
