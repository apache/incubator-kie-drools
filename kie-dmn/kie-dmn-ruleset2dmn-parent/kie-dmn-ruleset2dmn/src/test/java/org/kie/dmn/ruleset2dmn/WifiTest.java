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

public class WifiTest {
    
    @Test
    public void test() throws Exception {
        final String dmnXml = Converter.parse("wifi", this.getClass().getResourceAsStream("/wifi.pmml"));
        // Files.write(new File("src/test/resources/wifi.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Arrays.asList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [X4 <= -61.0] ^ [X5 <= -63.0] -> 1
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X4\" : -61, \"X5\": -63}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("1"));

        // [X1 >= -54.0] -> 2
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X1\" : -54}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("2"));

        // [X5 <= -57.0] ^ [X1 <= -46.0] ^ [X3 >= -53.0] ^ [X1 >= -55.0] ^ [X7 <= -73.0] -> 3
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"X5\" : -57, \"X1\": -46, \"X3\": -53, \"X7\": -73}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("3"));

        // // [X3 >= -51.0] ^ [X3 <= -51.0] ^ [X1 >= -43.0] ^ [X1 <= -43.0] -> 3
        // assertThat(dmnRuntime .evaluateAll(dmnRuntime.getModels().get(0),
        //         ctxFromJson(dmnRuntime, "{\"X1\" : -43, \"X3\": -51}")) // TODO capture this cases [x..x] in the Converter?
        // .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("2"));

        // default -> 0
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"nothing\" : 999}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("4"));
    }
}
