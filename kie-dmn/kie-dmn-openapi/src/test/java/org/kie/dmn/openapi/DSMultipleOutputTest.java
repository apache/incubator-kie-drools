package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

public class DSMultipleOutputTest extends BaseDMNOASTest {

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = createRuntime("DSMultipleOutput.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_A289CCD2-6759-47F6-87CD-F8E12880053F", "DSMultipleOutput");
        ObjectNode syntheticJSONSchema = synthesizeSchemaForDS(result, modelUnderTest, "DecisionService-1");
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"a name\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"a name\":\"John Doe\" }")).isEmpty();
    }

}
