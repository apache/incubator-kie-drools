package org.kie.dmn.feel.runtime.functions;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.functions.extended.NowFunction;


import static org.assertj.core.api.Assertions.assertThat;

public class NowFunctionTest {

    private NowFunction nowFunction;

    @Before
    public void setUp() {
        nowFunction = new NowFunction();
    }

    @Test
    public void invoke() {
        // The current time that we need to compare will almost never be the same as another one we get for comparison purposes,
        // because there is some execution between them, so the comparison assertion doesn't make sense.
        // Note: We cannot guarantee any part of the date to be the same. E.g. in case when the test is executed
        // at the exact moment when the year is flipped to the next one, we cannot guarantee the year will be the same.

        final FEELFnResult<TemporalAccessor> nowResult = nowFunction.invoke();
        assertThat(nowResult.isRight()).isTrue();
        final TemporalAccessor result = nowResult.cata(left -> null, right -> right);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOfAny(ZonedDateTime.class);
    }

}