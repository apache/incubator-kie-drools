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

public class MiniloanTest {
    
    @Test
    public void test() throws Exception {
        String dmnXml = Converter.parse("miniloan", this.getClass().getResourceAsStream("/miniloan.pmml"));
        // Files.write(new File("src/test/resources/miniloan.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [loanAmount <= 224145.0] ^ [income >= 80356.0] ^ [monthDuration >= 6.0] -> ` true`
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"loanAmount\" : 100000, \"income\": 85000, \"monthDuration\": 6}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(" true");

        // default -> ` false`
        assertThat(dmnRuntime .evaluateAll(modelUnderTest,
                ctxFromJson(modelUnderTest, "{\"loanAmount\" : 999000, \"income\": 85000, \"monthDuration\": 6}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(" false");
    }
}
