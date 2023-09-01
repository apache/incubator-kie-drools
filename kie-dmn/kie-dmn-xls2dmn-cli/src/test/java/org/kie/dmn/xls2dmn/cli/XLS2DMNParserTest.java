package org.kie.dmn.xls2dmn.cli;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.xls2dmn.cli.TestUtils.validateRuntime;

@RunWith(Parameterized.class)
public class XLS2DMNParserTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(XLS2DMNParserTest.class);

    @Parameters(name = "file: {0}")
    public static Iterable<? extends Object> data() {
        return Arrays.asList("/Loan_approvals.xlsx", "/Loan_approvals_colshuffled.xlsx");
    }

    private String filename;
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;
    
    public XLS2DMNParserTest(String filename) {
        this.filename = filename;
    }

    private DMNRuntime getRuntimeLoanApprovalXslx() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        new XLS2DMNParser(tempFile).parseFile(this.getClass().getResourceAsStream(filename));

        return validateRuntime(tempFile);
    }

    @Before
    public void init() throws Exception {
        dmnRuntime = getRuntimeLoanApprovalXslx();
        dmnModel = dmnRuntime.getModels().get(0);
    }

    @Test
    public void testLoanApprovalXslx() {
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("DTI Ratio", 1);
        dmnContext.set("PITI Ratio", 1);
        dmnContext.set("FICO Score", 650);
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Loan Approval").getResult()).isEqualTo("Not approved");
    }
    
    @Test
    public void testLoanApprovalXslx_Approved() {
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("DTI Ratio", .1);
        dmnContext.set("PITI Ratio", .1);
        dmnContext.set("FICO Score", 800);
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Loan Approval").getResult()).isEqualTo("Approved");
    }
}