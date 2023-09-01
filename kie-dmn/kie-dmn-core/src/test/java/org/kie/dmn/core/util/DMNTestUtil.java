package org.kie.dmn.core.util;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNTestUtil {

    private DMNTestUtil() {
        // No constructor for util class.
    }

    public static DMNModel getAndAssertModelNoErrors(final DMNRuntime runtime, final String namespace, final String modelName) {
        DMNModel dmnModel = runtime.getModel(namespace, modelName);
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        return dmnModel;
    }
}
