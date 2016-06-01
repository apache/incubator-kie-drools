/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests loading decision tables from several worksheets in a XLS file.
 */
public class MultipleSheetsLoadingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleSheetsLoadingTest.class);

    private static final String XLS_EXTENSION = "xls";
    private static final String XLS_FILE_NAME_NO_EXTENSION = "multiple-sheets";
    private static final String XLS_FILE_NAME = XLS_FILE_NAME_NO_EXTENSION + "." + XLS_EXTENSION;

    private static final String WORKSHEET_1_NAME = "first";
    private static final String WORKSHEET_2_NAME = "second";

    @Test
    public void test() {
        final KieBuilder kbuilder = this.buildResources();

        final Collection<Message> results = kbuilder.getResults().getMessages(Level.ERROR, Level.WARNING);
        if (results.size() > 0) {
            LOGGER.error(results.toString());
        }
        Assertions.assertThat(results).as("Some errors/warnings found").isEmpty();

        final KieBase kbase = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kbuilder);
        final StatelessKieSession ksession = kbase.newStatelessKieSession();

        final Set<String> resultSet = new HashSet<String>();
        ksession.execute((Object) resultSet);

        Assertions.assertThat(resultSet.size()).as("Wrong number of rules was fired").isEqualTo(2);
        for (String ruleName : new String[] { "rule1", "rule2" }) {
            Assertions.assertThat(resultSet.contains(ruleName)).as("Rule " + ruleName + " was not fired!").isTrue();
        }
    }

    private KieBuilder buildResources() {
        final Resource resourceXlsFirst = this.createResourceWithConfig(WORKSHEET_1_NAME);
        final Resource resourceXlsSecond = this.createResourceWithConfig(WORKSHEET_2_NAME);

        return KieBaseUtil.getKieBuilderFromResources(false, resourceXlsFirst, resourceXlsSecond);
    }

    private Resource createResourceWithConfig(final String worksheetName) {
        final Resource resourceXls =
                KieServices.Factory.get().getResources().newClassPathResource(XLS_FILE_NAME, getClass());
        resourceXls.setTargetPath(String.format("%s-%s.%s", XLS_FILE_NAME_NO_EXTENSION, worksheetName, XLS_EXTENSION));
        resourceXls.setConfiguration(this.createXLSResourceConfig(worksheetName));
        return resourceXls;
    }

    private ResourceConfiguration createXLSResourceConfig(final String worksheetName) {
        final DecisionTableConfiguration resourceConfig = new DecisionTableConfigurationImpl();
        resourceConfig.setInputType(DecisionTableInputType.XLS);
        resourceConfig.setWorksheetName(worksheetName);
        return resourceConfig;
    }
}
