package org.kie.dmn.feel.runtime.functions;


import org.junit.jupiter.api.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.util.Msg;

import static org.assertj.core.api.Assertions.assertThat;

class FEELFnResultTest {

    @Test
    void testOfWarnedResult() {
        FEELEvent warningEvent = new FEELEventBase( FEELEvent.Severity.WARN, Msg.createMessage(Msg.DEPRECATE_TIME_WITH_TIMEZONE), null);
        String value = "test";
        FEELFnResult<String> result = FEELFnResult.ofEventedResult(value, warningEvent);
        assertThat(result).isNotNull();
        assertThat(result.getEvent()).isEqualTo(warningEvent);
        assertThat(result.getOrElse(null)).isEqualTo(value);
    }
}
