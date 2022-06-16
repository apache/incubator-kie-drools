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

public class ChurnTest {

    @Test
    public void test() throws Exception {
        String dmnXml = Converter.parse("churn", this.getClass().getResourceAsStream("/churn.pmml"));
        Files.write(new File("src/test/resources/churn.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromClasspathResource("/churn.dmn", ChurnTest.class)
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new DMNRuntimeEventListener() {
            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                System.out.println("Decision Table selected:" + event.getSelected());
            }
        });

        // [Children <= 1.0] ^ [Gender == M] ^ [Age >= 39.213333] ^ [EstIncome >= 58328.8] ^ [EstIncome <= 95405.7] ^ [Age <= 63.933333] -> T
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 1, "M", 40, 75_000, "<unknown>", 0, "<unknown>", "<unknown>", 0))
                .getDecisionResults().get(0).getResult()).isEqualTo("T");

        // [Status == S] ^ [Gender == F] ^ [Usage >= 50.29] ^ [EstIncome <= 38000.0] -> T
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 1, "F", 0, 35_000, "S", 51, "<unknown>", "<unknown>", 0))
                .getDecisionResults().get(0).getResult()).isEqualTo("T");
        
        // default -> F
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                        churnCtx(dmnRuntime, 0, "<unknown>", 0, 35_000, "S", 51, "<unknown>", "<unknown>", 0))
                .getDecisionResults().get(0).getResult()).isEqualTo("F");
    }

    private DMNContext churnCtx(DMNRuntime dmnRuntime, double children, String gender, double age, double estIncome,
            String status, double usage, String paymethod, String carOwner, double ratePlan) {
        DMNContext ctx = dmnRuntime.newContext();
        ctx.set("Children", children);
        ctx.set("Gender", gender);
        ctx.set("Age", age);
        ctx.set("EstIncome", estIncome);
        ctx.set("Status", status);
        ctx.set("Usage", usage);
        ctx.set("Paymethod", paymethod);
        ctx.set("CarOwner", carOwner);
        ctx.set("RatePlan", ratePlan);
        return ctx;
    }
}
