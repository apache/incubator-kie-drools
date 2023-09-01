package org.kie.dmn.openapi;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.openapi.impl.DMNOASConstants.X_DMN_DESCRIPTIONS;

public class DMNDescriptionTest extends BaseDMNOASTest {

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = createRuntime("dmnDescription.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();
        JacksonUtils.printoutJSON(result.getJsonSchemaNode());

        JsonNode definitions = result.getJsonSchemaNode().get("definitions");
        assertThat(convertToMap((definitions.get("InputSet").get(X_DMN_DESCRIPTIONS)))).hasSize(1)
                                                                                       .containsEntry("in1", "description of in1");
        assertThat(convertToMap((definitions.get("OutputSet").get(X_DMN_DESCRIPTIONS)))).hasSize(3)
                                                                                        .containsEntry("in1", "description of in1")
                                                                                        .containsEntry("out1", "description of out1")
                                                                                        .containsEntry("d1", "description of d1");
        assertThat(convertToMap((definitions.get("InputSetDSmyDS1").get(X_DMN_DESCRIPTIONS)))).hasSize(2);
        assertThat(convertToMap((definitions.get("OutputSetDSmyDS1").get(X_DMN_DESCRIPTIONS)))).hasSize(1);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(JsonNode node) {
        return new ObjectMapper().convertValue(node, Map.class);
    }
}
