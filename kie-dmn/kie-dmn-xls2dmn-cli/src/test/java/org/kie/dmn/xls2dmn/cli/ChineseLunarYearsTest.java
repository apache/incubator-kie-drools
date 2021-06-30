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
import java.time.LocalDate;
import java.util.Arrays;

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

import picocli.CommandLine;

public class ChineseLunarYearsTest {

    private static final Logger LOG = LoggerFactory.getLogger(ChineseLunarYearsTest.class);

    private DMNRuntime getDMNRuntimeWithCLI() throws Exception {
        File tempFile = File.createTempFile("xls2dmn", ".dmn");
        new CommandLine(new App()).execute(new String[]{"src/test/resources/ChineseLunarYears.xlsx", tempFile.toString()});

        Either<Exception, DMNRuntime> fromResources = DMNRuntimeBuilder.fromDefaults()
                         .buildConfiguration()
                         .fromResources(Arrays.asList(ResourceFactory.newFileResource(tempFile)));

        LOG.info("{}", System.getProperty("java.io.tmpdir"));
        LOG.info("{}", tempFile);
        DMNRuntime dmnRuntime = fromResources.getOrElseThrow(RuntimeException::new);
        return dmnRuntime;
    }

    @Test
    public void testCLI() throws Exception {
        final DMNRuntime dmnRuntime = getDMNRuntimeWithCLI();
        DMNModel dmnModel = dmnRuntime.getModels().get(0);

        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("Date", LocalDate.of(2021, 4, 1));
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getDecisionResultByName("Chinese Year").getResult(), is("Golden Ox"));
    }
}