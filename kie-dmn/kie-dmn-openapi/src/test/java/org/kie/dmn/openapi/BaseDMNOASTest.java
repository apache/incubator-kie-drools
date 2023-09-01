package org.kie.dmn.openapi;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import io.smallrye.openapi.runtime.io.JsonUtil;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.openapi.model.DMNOASResult;

public abstract class BaseDMNOASTest {

    protected DMNRuntime createRuntime(String string, Class<?> class1) {
        return BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK.createRuntime(string, class1);
    }

    protected DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
        return BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK.createRuntimeWithAdditionalResources(string, class1, string2);
    }
    
    private ObjectMapper mapper = JsonMapper.builder()
                                            .addModule(new JavaTimeModule())
                                            .build();

    protected JsonNode readJSON(String content) throws IOException {
        return mapper.readTree(content);
    }

    protected JsonSchema getJSONSchema(JsonNode schemaContent) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(com.networknt.schema.SpecVersion.VersionFlag.V4);
        return factory.getSchema(schemaContent);
    }

    protected Set<ValidationMessage> validateUsing(JsonSchema validator, String json) throws IOException {
        return validator.validate(readJSON(json));
    }

    protected ObjectNode synthesizeSchema(DMNOASResult result, DMNModel modelUnderTest) {
        DMNType InputSetTypeUT = result.lookupIOSetsByModel(modelUnderTest).getInputSet();
        return synthesizeSchema(result, InputSetTypeUT);
    }

    private ObjectNode synthesizeSchema(DMNOASResult result, DMNType InputSetTypeUT) {
        String dollarRef = result.getNamingPolicy().getRef(InputSetTypeUT);
        ObjectNode syntheticJSONSchema = result.getJsonSchemaNode().deepCopy();
        JsonUtil.stringProperty(syntheticJSONSchema, "$ref", dollarRef);
        JacksonUtils.printoutJSON(syntheticJSONSchema);
        return syntheticJSONSchema;
    }

    protected ObjectNode synthesizeSchemaForDS(DMNOASResult result, DMNModel modelUnderTest, String dsName) {
        DMNType InputSetTypeUT = result.lookupIOSetsByModel(modelUnderTest).lookupDSIOSetsByName(dsName).getDSInputSet();
        return synthesizeSchema(result, InputSetTypeUT);
    }

}
