package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

public class UndefinedTest extends BaseDMNOASTest {

    @Test
    public void testUndefinedIO() throws Exception {
        final DMNRuntime runtime = createRuntime("undefinedIO.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "undefinedIO");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"asd\": 0}")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"in1\": 123}")).isEmpty();
        assertThat(validateUsing(validator, "{ \"in1\": \"John Doe\"}")).isEmpty();
    }
}
