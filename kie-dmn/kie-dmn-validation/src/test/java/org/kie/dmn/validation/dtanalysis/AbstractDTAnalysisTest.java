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
package org.kie.dmn.validation.dtanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.ValidatorUtil;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;
import org.kie.dmn.validation.dtanalysis.model.Hyperrectangle;
import org.kie.dmn.validation.dtanalysis.model.Overlap;
import org.kie.dmn.validation.dtanalysis.utils.DTAnalysisMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractDTAnalysisTest extends AbstractValidatorTest {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractDTAnalysisTest.class);

    protected static DTAnalysis getAnalysis(List<DMNMessage> dmnMessages, String id) {
        assertThat(dmnMessages).as("Expected to find DTAnalysis but messages are empty.").isNotEmpty();

        if (LOG.isDebugEnabled() ) {
            LOG.debug("List<DMNMessage> dmnMessages: \n{}", ValidatorUtil.formatMessages(dmnMessages));
        }

        Map<String, DTAnalysis> as = new HashMap<>();
        for (DMNMessage dmnMessage : dmnMessages) {
            if (dmnMessage.getSourceId().equals(id) && dmnMessage instanceof DMNDTAnalysisMessage) {
                DMNDTAnalysisMessage dmndtAnalysisMessage = (DMNDTAnalysisMessage) dmnMessage;
                if (as.containsKey(id)) {
                    assertThat(as.get(id)).as("Inconsistency detected").isEqualTo(dmndtAnalysisMessage.getAnalysis());
                } else {
                    as.put(id, dmndtAnalysisMessage.getAnalysis());
                }
            }
        }

        DTAnalysis analysis = as.get(id);
        assertThat(analysis).as("Null analysis value for key.").isNotNull();

        debugAnalysis(analysis);

        return analysis;
    }

    protected static void debugValidatorMsg(List<DMNMessage> dmnMessages) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder sbGaps = new StringBuilder("Validator messages:\n");
        for (DMNMessage msg : dmnMessages) {
            sbGaps.append(String.format("%8s %s", msg.getLevel(), msg.getMessage()));
            sbGaps.append("\n");
        }
        LOG.debug(sbGaps.toString());
    }

    protected static void debugAnalysis(DTAnalysis analysis) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        StringBuilder sbGaps = new StringBuilder("\nGaps:\n");
        for (Hyperrectangle gap : analysis.getGaps()) {
            sbGaps.append(gap.toString());
            sbGaps.append("\n");
        }
        LOG.debug(sbGaps.toString());

        DefaultPrinterConfiguration printConfig = new DefaultPrinterConfiguration();
        printConfig.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.COLUMN_ALIGN_PARAMETERS, true));
        printConfig.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.COLUMN_ALIGN_FIRST_METHOD_CHAIN, true));

        Expression printGaps = DTAnalysisMeta.printGaps(analysis);
        LOG.debug("\n" + printGaps.toString(printConfig));

        StringBuilder sbOverlaps = new StringBuilder("\nOverlaps:\n");
        for (Overlap overlap : analysis.getOverlaps()) {
            sbOverlaps.append(overlap.toString());
            sbOverlaps.append("\n");
        }
        LOG.debug(sbOverlaps.toString());

        Expression printOverlaps = DTAnalysisMeta.printOverlaps(analysis);
        LOG.debug("\n" + printOverlaps.toString(printConfig));
    }
}
