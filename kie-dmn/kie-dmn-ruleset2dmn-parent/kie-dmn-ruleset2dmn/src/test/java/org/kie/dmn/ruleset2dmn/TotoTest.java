package org.kie.dmn.ruleset2dmn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Arrays;
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
import org.kie.internal.io.ResourceFactory;

public class TotoTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Test
    public void test() throws Exception {
        final String dmnXml = Converter.parse("toto", this.getClass().getResourceAsStream("/toto.pmml"));
        Files.write(new File("src/test/resources/toto.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Arrays.asList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new DMNRuntimeEventListener() {
            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                System.out.println("Decision Table selected:" + event.getSelected());
            }
        });

        // [toto0 < 0.1] ^ [toto2 == False] -> 1
        assertThat(dmnRuntime .evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"toto0\" : 0.01, \"toto2\": false}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("1"));

        // default -> 0
        assertThat(dmnRuntime .evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"toto0\" : 999, \"toto2\": true}"))
        .getDecisionResults().get(0).getResult()).isEqualTo(new BigDecimal("0"));
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
