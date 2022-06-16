package org.kie.dmn.ruleset2dmn;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.ruleset2dmn.TestUtils.ctxFromJson;

public class TotoTest {
    
    @Test
    public void test() throws Exception {
        final String dmnXml = Converter.parse("toto", this.getClass().getResourceAsStream("/toto.pmml"));
        // Files.write(new File("src/test/resources/toto.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Arrays.asList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [toto0 < 0.1] ^ [toto2 == False] -> 1
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"toto0\" : 0.01, \"toto2\": false}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("1"));

        // default -> 0
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"toto0\" : 999, \"toto2\": true}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("0"));
    }
}
