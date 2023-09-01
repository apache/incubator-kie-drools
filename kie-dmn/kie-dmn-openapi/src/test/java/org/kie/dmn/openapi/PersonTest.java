package org.kie.dmn.openapi;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest extends BaseDMNOASTest {

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = createRuntime("personF.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_4564F2BC-888D-472A-A38C-A861DA49A780", "personF");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"p\": {\"name\":\"John Doe\", \"favourites\":[{\"name\":\"red\", \"hex\": \"#FF0000\"}] }}")).isEmpty();
    }
}
