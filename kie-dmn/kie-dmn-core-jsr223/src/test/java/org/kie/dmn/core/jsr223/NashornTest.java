package org.kie.dmn.core.jsr223;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class NashornTest {
    private static final Logger LOG = LoggerFactory.getLogger( NashornTest.class );

    @Test
    public void testNashorn() {
        System.setProperty("nashorn.args", "--language=es6"); // TODO document: anyway is a partial ES6 support.
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
            .setDecisionLogicCompilerFactory(new JSR223EvaluatorCompilerFactory())
            .buildConfiguration()
            .fromClasspathResource("/Nashorn/BMI.dmn", this.getClass())
            .getOrElseThrow(e -> new RuntimeException(e));
        DMNModel model = dmnRuntime.getModels().get(0);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Height", 72);
        dmnContext.set("Mass", 180);
        DMNResult evaluateAll = dmnRuntime.evaluateAll(model, dmnContext);
        LOG.info("{}", evaluateAll.getContext());
        LOG.info("{}", evaluateAll.getMessages());
        assertThat(evaluateAll.getDecisionResultByName("BMI value classification").getResult()).isEqualTo("Normal range");
    }
}
