package org.kie.dmn.ruleset2dmn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.model.api.InputData;

public class MiniloanTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Test
    public void test() throws Exception {
        String dmnXml = Converter.parse("miniloan", this.getClass().getResourceAsStream("/miniloan.pmml"));
        Files.write(new File("src/test/resources/miniloan.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromClasspathResource("/miniloan.dmn", IrisTest.class)
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new DMNRuntimeEventListener() {
            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                System.out.println("Decision Table selected:" + event.getSelected());
            }
        });

        // [loanAmount <= 224145.0] ^ [income >= 80356.0] ^ [monthDuration >= 6.0] -> ` true`
        assertThat(dmnRuntime .evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"loanAmount\" : 100000, \"income\": 85000, \"monthDuration\": 6}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(" true");

        // default -> ` false`
        assertThat(dmnRuntime .evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"loanAmount\" : 999000, \"income\": 85000, \"monthDuration\": 6}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(" false");
    }

    private DMNContext ctxFromJson(DMNRuntime dmnRuntime, String json) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonMap = MAPPER.readValue(json, Map.class);
        DMNContext ctx = dmnRuntime.newContext();
        Collection<InputData> ids = dmnRuntime.getModels().get(0).getDefinitions().getDrgElement().stream()
                        .filter(InputData.class::isInstance).map(InputData.class::cast)
                        .collect(Collectors.toList());
        for (InputData id : ids) {
            Object valForId = jsonMap.get(id.getName());
            if (valForId == null && (!id.getVariable().getTypeRef().toString().equals("number") && !id.getVariable().getTypeRef().toString().equals("boolean"))) {
                valForId = "<unknown>";
            }
            ctx.set(id.getName(), valForId);
        }
        return ctx;
    }
}
