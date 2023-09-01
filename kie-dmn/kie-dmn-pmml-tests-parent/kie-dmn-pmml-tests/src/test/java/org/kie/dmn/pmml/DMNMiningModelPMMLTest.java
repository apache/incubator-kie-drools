package org.kie.dmn.pmml;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DMNMiningModelPMMLTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNMiningModelPMMLTest.class);

    @Test
    public void testMiningModelSum() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("SumMiningModel.dmn",
                                                                                       DMNMiningModelPMMLTest.class,
                                                                                       "test_mining_model_summed.pmml");


        assertThat(evaluateMiningModelDecision(runtime, 10, 10, 10))
                .isEqualTo(new BigDecimal(2070));
        assertThat(evaluateMiningModelDecision(runtime, 200, -1, 2))
                .isEqualTo(new BigDecimal(-299));
        assertThat(evaluateMiningModelDecision(runtime, 90, 2, 4))
                .isEqualTo(new BigDecimal(17040));
    }

    private BigDecimal evaluateMiningModelDecision(final DMNRuntime runtime, final double input1, final double input2,
                                                   final double input3) {
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_0E8EC382-BB89-4877-8D37-A59B64285F05", "MiningModelDMN");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).isFalse();

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("input1", input1);
        dmnContext.set("input2", input2);
        dmnContext.set("input3", input3);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isFalse();

        final DMNContext resultContext = dmnResult.getContext();
        assertThat(resultContext).isNotNull();
        assertThat(resultContext.get("Decision")).isInstanceOf(BigDecimal.class);
        final BigDecimal result = (BigDecimal) resultContext.get("Decision");
        
        return result;
    }
}
