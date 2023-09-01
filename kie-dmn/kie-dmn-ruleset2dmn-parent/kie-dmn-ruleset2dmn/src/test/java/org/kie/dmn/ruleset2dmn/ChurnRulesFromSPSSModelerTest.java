package org.kie.dmn.ruleset2dmn;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.ruleset2dmn.TestUtils.ctxFromJson;

public class ChurnRulesFromSPSSModelerTest {
    
    @Test
    public void test() throws Exception {
        final String modelName = "ChurnSPSS";
        final String dmnXml = Converter.parse(modelName, this.getClass().getResourceAsStream("/Churn Rules from SPSS Modeler.xml"));
        // Files.write(new File("src/test/resources/ChurnSPSS.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // 1st, 2nd
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"Gender\": \"F\", \"Status\": \"S\", \"Paymethod\": \"CC\", \"Est Income\": 21000, \"Local\": 11}"))
        .getDecisionResultByName(modelName).getResult()).isEqualTo("T");

        // second-to-last and last
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"LongDistance\": 9, \"Gender\": \"M\", \"Est Income\": 44000}"))
        .getDecisionResultByName(modelName).getResult()).isEqualTo("F");

        // only last
        assertThat(dmnRuntime.evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"LongDistance\": 9}"))
        .getDecisionResultByName(modelName).getResult()).isEqualTo("F");
    }
}
