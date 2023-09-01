package org.kie.dmn.openapi;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberAllowedValuesTest extends BaseDMNOASTest {

    @Test
    public void testAllowedValues() throws Exception {
        final DMNRuntime runtime = createRuntime("numberAllowedValues.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "numberAllowedValues");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"gt0\": 123, \"lteq47gt0\": 47, \"r0100\": 0}")).isEmpty();
    }

    @Test
    public void testNumberList() throws Exception {
        final DMNRuntime runtime = createRuntime("numberList.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "numberList");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"in1\": 47 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"in1\": 0 }")).isEmpty();
    }

    @Test
    public void testGT() throws Exception {
        final DMNRuntime runtime = createRuntime("numberGT.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "numberGT");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"age\": 47, \"negative\" : -47 }")).isEmpty();

        JsonNode tAge = syntheticJSONSchema.get("definitions").get("tAge");
        assertThat(tAge.fieldNames()).toIterable().doesNotContain("exclusiveMaximum", "maximum");
        JsonNode tNegative = syntheticJSONSchema.get("definitions").get("tNegative");
        assertThat(tNegative.fieldNames()).toIterable().doesNotContain("exclusiveMinumum", "minumum");
    }
}
