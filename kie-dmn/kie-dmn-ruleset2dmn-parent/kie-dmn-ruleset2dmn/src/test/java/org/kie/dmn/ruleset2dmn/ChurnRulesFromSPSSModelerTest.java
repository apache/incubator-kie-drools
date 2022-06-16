package org.kie.dmn.ruleset2dmn;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.model.api.InputData;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChurnRulesFromSPSSModelerTest {
    private static final Logger LOG = LoggerFactory.getLogger(ChurnRulesFromSPSSModelerTest.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Test
    public void test() throws Exception {
        final String modelName = "ChurnSPSS";
        final String dmnXml = Converter.parse(modelName, this.getClass().getResourceAsStream("/Churn Rules from SPSS Modeler.xml"));
        LOG.trace("{}", dmnXml);
        Files.write(new File("src/test/resources/ChurnSPSS.dmn").toPath(), dmnXml.getBytes());
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(Arrays.asList(ResourceFactory.newByteArrayResource(dmnXml.getBytes())))
                .getOrElseThrow(RuntimeException::new);
        dmnRuntime.addListener(new DMNRuntimeEventListener() {
            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                LOG.debug("Decision Table selected: {}", event.getSelected());
            }
            @Override
            public void afterEvaluateAll(AfterEvaluateAllEvent event) {
                LOG.debug("OUTPUT: {}", event.getResult().getContext());
            }

        });

        // 1st, 2nd
        assertThat(dmnRuntime.evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"Gender\": \"F\", \"Status\": \"S\", \"Paymethod\": \"CC\", \"Est Income\": 21000, \"Local\": 11}"))
        .getDecisionResultByName(modelName).getResult()).isEqualTo("T");

        // second-to-last and last
        assertThat(dmnRuntime.evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"LongDistance\": 9, \"Gender\": \"M\", \"Est Income\": 44000}"))
        .getDecisionResultByName(modelName).getResult()).isEqualTo("F");

        // only last
        assertThat(dmnRuntime.evaluateAll(dmnRuntime.getModels().get(0),
                ctxFromJson(dmnRuntime, "{\"LongDistance\": 9}"))
        .getDecisionResultByName(modelName).getResult()).isEqualTo("F");
    }

    private DMNContext ctxFromJson(DMNRuntime dmnRuntime, String json) throws Exception {
        LOG.debug("INPUT: {}", json);
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
