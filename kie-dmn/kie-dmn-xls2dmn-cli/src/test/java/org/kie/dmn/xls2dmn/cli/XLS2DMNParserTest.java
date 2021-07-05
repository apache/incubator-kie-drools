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
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.feel.util.Either;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class XLS2DMNParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(XLS2DMNParserTest.class);
    private DMNRuntime dmnRuntime;
    private DMNModel dmnModel;

    private DMNRuntime getRuntimeLoanApprovalXslx() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        new XLS2DMNParser(tempFile).parseFile(this.getClass().getResourceAsStream("/Loan_approvals.xlsx"));

        Either<Exception, DMNRuntime> fromResources = DMNRuntimeBuilder.fromDefaults()
                         .buildConfiguration()
                         .fromResources(Arrays.asList(ResourceFactory.newFileResource(tempFile)));

        LOG.info("{}", System.getProperty("java.io.tmpdir"));
        DMNRuntime dmnRuntime = fromResources.getOrElseThrow(RuntimeException::new);
        return dmnRuntime;
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
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Loan Approval").getResult(), is("Not approved"));
    }
    
    @Test
    public void testLoanApprovalXslx_Approved() {
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("DTI Ratio", .1);
        dmnContext.set("PITI Ratio", .1);
        dmnContext.set("FICO Score", 800);
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Loan Approval").getResult(), is("Approved"));
    }
}