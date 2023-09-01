package org.kie.dmn.core.extra;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNRecursionTest extends BaseInterpretedVsCompiledTest {

    public DMNRecursionTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRecursionTest.class);

    @Test
    public void testBasicRecursion() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("basicRecursion.dmn", this.getClass());
        runtime.addListener(new DMNRuntimeEventListener() {});
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_16EF6BF4-9B59-40E8-8C99-A3A3D58B88CC", "factorial");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("My number", 3);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("compute factorial").getResult()).isEqualTo(new BigDecimal(6));
    }
}

