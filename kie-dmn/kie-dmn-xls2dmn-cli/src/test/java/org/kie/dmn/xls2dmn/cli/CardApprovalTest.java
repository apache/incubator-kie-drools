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
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.xls2dmn.cli.TestUtils.getRuntime;

class CardApprovalTest {

    static final Logger LOG = LoggerFactory.getLogger(CardApprovalTest.class);

    private DMNRuntime getDMNRuntimeWithCLI() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        return getRuntime(new CommandLine(new App())::execute, tempFile, new String[]{"src/test/resources/Card_approval.xlsx", tempFile.toString()});
    }

    private DMNRuntime getDMNRuntimeWithSameVMApp() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        return getRuntime(SameVMApp::main, tempFile, new String[]{"src/test/resources/Card_approval.xlsx", tempFile.toString()});
    }

    @Test
    void cli() throws Exception {
        final DMNRuntime dmnRuntime = getDMNRuntimeWithCLI();
        checkCardApprovalDMNModel(dmnRuntime);
    }

    @Test
    void sameVMApp() throws Exception {
        final DMNRuntime dmnRuntime = getDMNRuntimeWithSameVMApp();
        checkCardApprovalDMNModel(dmnRuntime);
    }

    private void checkCardApprovalDMNModel(final DMNRuntime dmnRuntime) {
        DMNModel dmnModel = dmnRuntime.getModels().get(0);
        assertThat(dmnModel.getName()).isEqualTo("Card_approval");
        assertThat(dmnModel.getDefinitions().getId()).isNotNull();

        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Annual Income", 70);
        dmnContext.set("Assets", 150);
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();
        assertThat(dmnResult.getDecisionResultByName("Standard card score").getResult()).isEqualTo(new BigDecimal(562));
        assertThat(dmnResult.getDecisionResultByName("Gold card score").getResult()).isEqualTo(new BigDecimal(468));
    }


}