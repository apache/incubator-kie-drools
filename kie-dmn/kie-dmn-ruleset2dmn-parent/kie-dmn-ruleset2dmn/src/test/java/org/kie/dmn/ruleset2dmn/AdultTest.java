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

public class AdultTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void test() throws Exception {
        String dmnXml = Converter.parse("adult", this.getClass().getResourceAsStream("/adult.pmml"));
        Files.write(new File("src/test/resources/adult.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                        .buildConfiguration()
                        .fromClasspathResource("/adult.dmn", AdultTest.class)
                        .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new DMNRuntimeEventListener() {
                @Override
                public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                        System.out.println("Decision Table selected:" + event.getSelected());
                }
        });

        // [marital_status == Married-civ-spouse] ^ [capital_gain >= 5060.0] -> >50K
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                                ctxFromJson(dmnRuntime, "{\"marital_status\" : \"Married-civ-spouse\", \"capital_gain\" : 5060}"))
                .getDecisionResults().get(0).getResult()).isEqualTo(">50K");

        // default -> <=50K
        assertThat(dmnRuntime
                .evaluateAll(dmnRuntime.getModels().get(0),
                                ctxFromJson(dmnRuntime, "{\"marital_status\" : null, \"capital_gain\" : 5060}"))
                .getDecisionResults().get(0).getResult()).isEqualTo("<=50K");
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
