package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeWithSpaceTest extends BaseDMNOASTest {

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = createRuntime("typeWithSpace.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("ns1", "typeWithSpace");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"a Person\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"a Person\": { \"full name\":123 } }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"a Person\": { \"full name\": \"John Doe\" } }")).isEmpty();
    }
}
