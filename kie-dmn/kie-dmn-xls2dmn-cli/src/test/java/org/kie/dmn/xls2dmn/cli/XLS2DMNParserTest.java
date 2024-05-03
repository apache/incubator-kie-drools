/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.xls2dmn.cli;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

    protected static Iterable<? extends Object> data() {
        return Arrays.asList("/Loan_approvals.xlsx", "/Loan_approvals_colshuffled.xlsx");
    }

    private String filename;
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;

    private DMNRuntime getRuntimeLoanApprovalXslx() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        new XLS2DMNParser(tempFile).parseFile(this.getClass().getResourceAsStream(filename));

        return validateRuntime(tempFile);
    }

    void init(String filename) throws Exception {
        this.filename = filename; 
        dmnRuntime = getRuntimeLoanApprovalXslx();
        dmnModel = dmnRuntime.getModels().get(0);
    }

    @MethodSource("data")
    @ParameterizedTest(name = "file: {0}")
    public void loanApprovalXslx(String filename) throws Exception {
        init(filename);
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("DTI Ratio", 1);
        dmnContext.set("PITI Ratio", 1);
        dmnContext.set("FICO Score", 650);
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Loan Approval").getResult()).isEqualTo("Not approved");
    }

    @MethodSource("data")
    @ParameterizedTest(name = "file: {0}")
    public void loanApprovalXslxApproved(String filename) throws Exception {
        init(filename);
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