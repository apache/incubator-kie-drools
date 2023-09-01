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

public class AdultTest {

    @Test
    public void test() throws Exception {
        String dmnXml = Converter.parse("adult", this.getClass().getResourceAsStream("/adult.pmml"));
        // Files.write(new File("src/test/resources/adult.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                        .buildConfiguration()
                        .fromResources(Collections.singletonList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                        .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [marital_status == Married-civ-spouse] ^ [capital_gain >= 5060.0] -> >50K
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                                ctxFromJson(modelUnderTest, "{\"marital_status\" : \"Married-civ-spouse\", \"capital_gain\" : 5060}"))
                .getDecisionResults().get(0).getResult()).isEqualTo(">50K");

        // default -> <=50K
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                                ctxFromJson(modelUnderTest, "{\"marital_status\" : null, \"capital_gain\" : 5060}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("<=50K");
    }
}
