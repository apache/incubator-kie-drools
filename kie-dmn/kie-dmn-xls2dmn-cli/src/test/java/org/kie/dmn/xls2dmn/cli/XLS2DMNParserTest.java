/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.xls2dmn.cli;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.xls2dmn.cli.TestUtils.validateRuntime;

public class XLS2DMNParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(XLS2DMNParserTest.class);
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;

    private DMNRuntime getRuntimeLoanApprovalXslx() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        new XLS2DMNParser(tempFile).parseFile(this.getClass().getResourceAsStream("/Loan_approvals.xlsx"));

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