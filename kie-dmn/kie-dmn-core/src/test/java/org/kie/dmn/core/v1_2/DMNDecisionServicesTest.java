package org.kie.dmn.core.v1_2;

import org.junit.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNDecisionServicesTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNDecisionServicesTest.class);

    public DMNDecisionServicesTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDecisionServiceCompiler20180830_DMN12() {
        // DROOLS-2943 DMN DecisionServiceCompiler not correctly wired for DMNv1.2 format
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("DecisionServiceABC_DMN12.dmn", this.getClass());
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_2443d3f5-f178-47c6-a0c9-b1fd1c933f60", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        org.kie.dmn.core.decisionservices.DMNDecisionServicesTest.testDecisionServiceCompiler20180830_testEvaluateDS(runtime, dmnModel);
        org.kie.dmn.core.decisionservices.DMNDecisionServicesTest.testDecisionServiceCompiler20180830_testEvaluateAll(runtime, dmnModel);
    }

}
