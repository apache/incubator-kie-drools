package org.kie.dmn.ruleset2dmn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;

public class IrisTest {

    @Test
    public void test() throws Exception {
        String dmnXml = Converter.parse("iris", this.getClass().getResourceAsStream("/iris.pmml"));
        Files.write(new File("src/test/resources/iris.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromClasspathResource("/iris.dmn", IrisTest.class)
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new DMNRuntimeEventListener() {
            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                System.out.println("Decision Table selected:" + event.getSelected());
            }
        });

        // [petal_length >= 3.0] ^ [petal_length <= 4.9] ^ [petal_width <= 1.6] -> Iris-versicolor
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 0.1, 0.1, 3.5, 0.1))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-versicolor");

        // [petal_length <= 1.7] -> Iris-setosa
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 0.1, 0.1, 0.1, 0.1))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-setosa");

        // [sepal_width <= 2.9] ^ [petal_width <= 1.3] ^ [sepal_length >= 4.6] -> Iris-versicolor
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 47, 0.1, 47, 0.1))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-versicolor");
        
        // default Iris-virginica
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 999, 999, 999, 999))
                .getDecisionResults().get(0).getResult()).isEqualTo("Iris-virginica");
    }

    private DMNContext churnCtx(DMNRuntime dmnRuntime, double sepal_length, double sepal_width, double petal_length, double petal_width) {
        DMNContext ctx = dmnRuntime.newContext();
        ctx.set("sepal_length", sepal_length);
        ctx.set("sepal_width", sepal_width);
        ctx.set("petal_length", petal_length);
        ctx.set("petal_width", petal_width);
        return ctx;
    }
}
