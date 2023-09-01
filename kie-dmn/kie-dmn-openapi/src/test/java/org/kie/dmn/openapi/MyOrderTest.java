package org.kie.dmn.openapi;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

public class MyOrderTest extends BaseDMNOASTest {

    @Test
    public void test() throws Exception {
        final DMNRuntime runtime = createRuntime("myOrder.dmn", this.getClass());
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("https://kiegroup.org/dmn/_5674F1B8-13A1-4EA8-8669-42F02C5E2667", "myOrder");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        JsonSchema validator = getJSONSchema(syntheticJSONSchema);

        assertThat(validateUsing(validator, "{ \"an order\":123 }")).isNotEmpty();
        assertThat(validateUsing(validator, "{ \"an order\": { \"header\": \"John Doe's whishlist\", \"items\" : [ { \"name\": \"a\", \"qty\": 1 }, { \"name\": \"b\", \"qty\": 2 } ] } }")).isEmpty();
    }
}
