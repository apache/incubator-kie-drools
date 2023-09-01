package org.kie.dmn.core.extra;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class YCombinatorTest extends BaseInterpretedVsCompiledTest {

    public YCombinatorTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    public static final Logger LOG = LoggerFactory.getLogger(YCombinatorTest.class);

    @Test
    public void testY() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Y.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_2E160C58-B13A-4C35-B161-BB4B31E049B4",
                                                   "new-file");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("fac3").getResult()).isEqualTo(new BigDecimal(6));
        assertThat(dmnResult.getDecisionResultByName("fib5").getResult()).asList().containsExactly(new BigDecimal(1),
                                                                                           new BigDecimal(1),
                                                                                           new BigDecimal(2),
                                                                                           new BigDecimal(3),
                                                                                           new BigDecimal(5));
    }

    @Test
    public void testYboxed() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("Yboxed.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_2E160C58-B13A-4C35-B161-BB4B31E049B4",
                                                   "new-file");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext ctx = runtime.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("fac3").getResult()).isEqualTo(new BigDecimal(6));
        assertThat(dmnResult.getDecisionResultByName("fib5").getResult()).asList().containsExactly(new BigDecimal(1),
                                                                                           new BigDecimal(1),
                                                                                           new BigDecimal(2),
                                                                                           new BigDecimal(3),
                                                                                           new BigDecimal(5));
    }

}

