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

public class IrisTest {

    @Test
    public void test() throws Exception {
        final String dmnXml = Converter.parse("iris", this.getClass().getResourceAsStream("/iris.pmml"));
        // Files.write(new File("src/test/resources/iris.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Collections.singletonList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new TestDMNRuntimeEventListener());
        final DMNModel modelUnderTest = dmnRuntime.getModels().get(0);

        // [petal_length >= 3.0] ^ [petal_length <= 4.9] ^ [petal_width <= 1.6] -> Iris-versicolor
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"sepal_length\": 0.1, \"sepal_width\": 0.1, \"petal_length\": 3.5, \"petal_width\": 0.1}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-versicolor");

        // [petal_length <= 1.7] -> Iris-setosa
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"sepal_length\": 0.1, \"sepal_width\": 0.1, \"petal_length\": 0.1, \"petal_width\": 0.1}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-setosa");

        // [sepal_width <= 2.9] ^ [petal_width <= 1.3] ^ [sepal_length >= 4.6] -> Iris-versicolor
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"sepal_length\": 47, \"sepal_width\": 0.1, \"petal_length\": 47, \"petal_width\": 0.1}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-versicolor");
        
        // default Iris-virginica
        assertThat(dmnRuntime
                .evaluateAll(modelUnderTest,
                        ctxFromJson(modelUnderTest, "{ \"sepal_length\": 999, \"sepal_width\": 999, \"petal_length\": 999, \"petal_width\": 999}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-virginica");
    }
}
