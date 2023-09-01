package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonsTest extends BaseDMNOASTest {

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = createRuntime("persons.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_4766A842-0524-4727-979B-45BF678F2F36", "persons");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"ps1\": [{\"name\":\"John Doe\", \"age\": 47}], \"ps2\": [{\"name\":\"John Doe\", \"age\": 47}] }")).isEmpty();
    }

    @Test
    public void testForPrefix() throws Exception {
        final DMNRuntime runtime = createRuntime("persons.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels(), "#/definitions/").build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_4766A842-0524-4727-979B-45BF678F2F36", "persons");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"ps1\": [{\"name\":\"John Doe\", \"age\": 47}], \"ps2\": [{\"name\":\"John Doe\", \"age\": 47}] }")).isEmpty();
    }
}
