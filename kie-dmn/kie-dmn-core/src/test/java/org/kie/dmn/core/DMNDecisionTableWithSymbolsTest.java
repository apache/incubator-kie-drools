package org.kie.dmn.core;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDecisionTableWithSymbolsTest extends BaseInterpretedVsCompiledTest {

    public DMNDecisionTableWithSymbolsTest(final boolean useExecModelCompiler ) {
        super( useExecModelCompiler );
    }

    @Test
    public void testDecisionWithArgumentsOnOutput() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Decide with symbols.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_79b16a68-013b-484c-98f5-49ff77808800", "Decide with symbols");
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();
        context.set("Person age", 44);
        context.set("Person name", "Mario");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Decide with symbol")).isEqualTo("Hello, Mario");
    }
}
