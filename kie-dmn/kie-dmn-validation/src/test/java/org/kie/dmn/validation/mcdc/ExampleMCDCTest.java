/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.validation.mcdc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.validation.dtanalysis.AbstractDTAnalysisTest;
import org.kie.dmn.validation.dtanalysis.MCDCAnalyser.PosNegBlock;
import org.kie.dmn.validation.dtanalysis.MCDCAnalyser.Record;
import org.kie.dmn.validation.dtanalysis.model.DTAnalysis;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.ANALYZE_DECISION_TABLE;
import static org.kie.dmn.validation.DMNValidator.Validation.COMPUTE_DECISION_TABLE_MCDC;

public class ExampleMCDCTest extends AbstractDTAnalysisTest {

    @Test
    public void test1() {
        final String resourceFileName = "example1.dmn";
        List<DMNMessage> validate = validator.validate(getReader(resourceFileName), ANALYZE_DECISION_TABLE, COMPUTE_DECISION_TABLE_MCDC);

        DTAnalysis analysis = getAnalysis(validate, "_452a0adf-dd49-47c3-b02d-fe0ad45902c7");
        Collection<Record> mcdcCases = computeMCDCCases(analysis.getMCDCSelectedBlocks());
        assertThat(mcdcCases, hasSize(16));

        assertMCDCCases(resourceFileName, analysis.getSource(), mcdcCases);
    }

    @Test
    public void test2() {
        final String resourceFileName = "example2.dmn";
        List<DMNMessage> validate = validator.validate(getReader(resourceFileName), ANALYZE_DECISION_TABLE, COMPUTE_DECISION_TABLE_MCDC);

        DTAnalysis analysis = getAnalysis(validate, "_e31c78b7-63ef-4112-a0bc-b0546043ebe9");
        Collection<Record> mcdcCases = computeMCDCCases(analysis.getMCDCSelectedBlocks());
        assertThat(mcdcCases, hasSize(14));

        assertMCDCCases(resourceFileName, analysis.getSource(), mcdcCases);
    }

    public static class MCDCListener implements DMNRuntimeEventListener {

        /** The listener reports the ID accordingly to the DMN Specification, that is 1-based. */
        public final List<Integer> selectedRule = new ArrayList<>();

        @Override
        public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
            selectedRule.addAll(event.getSelected());
        }

    }

    private void assertMCDCCases(final String resourceFileName, DecisionTable sourceDT, Collection<Record> mcdcCases) {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resourceFileName, ExampleMCDCTest.class);
        MCDCListener mcdcListener = new MCDCListener();
        runtime.addListener(mcdcListener);
        DMNModel dmnModel = runtime.getModels().get(0);
        assertThat(dmnModel, notNullValue());

        for (Record mcdcCase : mcdcCases) {
            mcdcListener.selectedRule.clear();
            DMNContext context = runtime.newContext();
            for (int i = 0; i < mcdcCase.enums.length; i++) {
                context.set(sourceDT.getInput().get(i).getInputExpression().getText(), mcdcCase.enums[i]);
            }
            DMNResult evaluateAll = runtime.evaluateAll(dmnModel, context);
            LOG.debug("{}", evaluateAll);
            assertThat(mcdcListener.selectedRule, hasItems(mcdcCase.ruleIdx + 1));
        }
    }

    private Collection<Record> computeMCDCCases(List<PosNegBlock> mcdcSelectedBlocks) {
        Set<Record> mcdcRecords = new LinkedHashSet<>();
        for (PosNegBlock b : mcdcSelectedBlocks) {
            mcdcRecords.add(b.posRecord);
            for (Record negRecord : b.negRecords) {
                mcdcRecords.add(negRecord);
            }
        }
        return mcdcRecords;
    }
}
