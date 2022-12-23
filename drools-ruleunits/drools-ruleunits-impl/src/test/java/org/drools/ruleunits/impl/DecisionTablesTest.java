/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.impl;

import java.util.List;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.impl.domain.Applicant;
import org.drools.ruleunits.impl.domain.LoanApplication;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionTablesTest {

    @Test
    public void decisionTable_xls() throws Exception {
        LoanUnit loanUnit = new LoanUnit();
        loanUnit.setMaxAmount(5000);

        try (RuleUnitInstance<LoanUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(loanUnit);) {
            Applicant applicant1 = new Applicant("John", 30);
            LoanApplication loanApplication1 = new LoanApplication("A001", applicant1, 2000, 100);
            loanUnit.getLoanApplications().add(loanApplication1);
            Applicant applicant2 = new Applicant("Paul", 29);
            LoanApplication loanApplication2 = new LoanApplication("A002", applicant2, 5000, 100);
            loanUnit.getLoanApplications().add(loanApplication2);
            Applicant applicant3 = new Applicant("George", 27);
            LoanApplication loanApplication3 = new LoanApplication("A003", applicant3, 5000, 1000);
            loanUnit.getLoanApplications().add(loanApplication3);

            List<LoanApplication> queryResult = instance.executeQuery("FindApproved").toList("$l");

            assertThat(queryResult).containsExactlyInAnyOrder(loanApplication1, loanApplication3);
            assertThat(loanApplication1.isApproved()).isTrue();
            assertThat(loanApplication2.isApproved()).isFalse();
            assertThat(loanApplication3.isApproved()).isTrue();
        }
    }
}
