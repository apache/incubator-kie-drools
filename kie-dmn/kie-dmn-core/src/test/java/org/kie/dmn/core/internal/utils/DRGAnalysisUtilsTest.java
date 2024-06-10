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
package org.kie.dmn.core.internal.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseVariantTest;
import org.kie.dmn.core.internal.utils.DRGAnalysisUtils.DRGDependency;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.v1_3.DMN13specificTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DRGAnalysisUtilsTest {

    public static final Logger LOG = LoggerFactory.getLogger(DRGAnalysisUtilsTest.class);

    protected DMNRuntime createRuntime(String string, Class<?> class1) {
        return BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK.createRuntime(string, class1);
    }

    protected DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
        return BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK.createRuntimeWithAdditionalResources(string, class1, string2);
    }

    @Test
    void ch11usingDMN13() throws Exception {
        final DMNRuntime runtime = createRuntimeWithAdditionalResources("Chapter 11 Example.dmn", DMN13specificTest.class, "Financial.dmn");
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_9d01a0c4-f529-4ad8-ad8e-ec5fb5d96ad4", "Chapter 11 Example");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();
        
        Collection<DRGDependency> reqMonthlyInstallment = DRGAnalysisUtils.dependencies(dmnModel, "Required monthly installment");
        assertThat(reqMonthlyInstallment).hasSize(3);
        assertThat(reqMonthlyInstallment.stream().map(d -> d.getDependency().getName()).collect(Collectors.toList())).containsExactlyInAnyOrder("Requested product", "Installment calculation", "PMT");
        assertThat(reqMonthlyInstallment.stream().filter(d -> d.getDependency().getName().equals("Requested product")).findFirst()).isPresent().get().hasFieldOrPropertyWithValue("degree", 0);
        assertThat(reqMonthlyInstallment.stream().filter(d -> d.getDependency().getName().equals("PMT")).findFirst()).isPresent().get().hasFieldOrPropertyWithValue("degree", 1);
        
        assertThatThrownBy(() -> DRGAnalysisUtils.inputDataOfDecision(dmnModel, "Routing rules")).isInstanceOf(IllegalArgumentException.class);
        
        Collection<String> adjudicationIDs = DRGAnalysisUtils.inputDataOfDecision(dmnModel, "Adjudication");
        assertThat(adjudicationIDs).containsExactlyInAnyOrder("Supporting documents", "Bureau data", "Requested product", "Applicant data");
        
        Collection<String> routingIDs = DRGAnalysisUtils.inputDataOfDecision(dmnModel, "Routing");
        assertThat(routingIDs).containsExactlyInAnyOrder("Bureau data", "Requested product", "Applicant data");
        
        Collection<String> strategyIDs = DRGAnalysisUtils.inputDataOfDecision(dmnModel, "Strategy");
        assertThat(strategyIDs).containsExactlyInAnyOrder("Requested product", "Applicant data");
    }
}