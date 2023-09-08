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
package org.drools.compiler.integrationtests.ruleunits.decisiontables;

import java.util.List;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DecisionTablesTest {

    // Below are rules generated from LoanUnit.drl.xls
    /*
    package org.drools.compiler.integrationtests.ruleunits.decisiontables;
    unit LoanUnit;
    import org.drools.compiler.integrationtests.ruleunits.decisiontables.LoanApplication;
    
    query FindApproved $l: /loanApplications[ approved ] end
    
    rule "LoanRule_13"
    when
        $l: /loanApplications[applicant.age >= 20, deposit < 1000, amount <= 2000]
    then
        modify($l) { setApproved(true) };
    end
    
    rule "LoanRule_14"
    when
        $l: /loanApplications[applicant.age >= 20, deposit < 1000, amount > 2000]
    then
        modify($l) { setApproved(false) };
    end
    
    rule "LoanRule_15"
    when
        $l: /loanApplications[applicant.age >= 20, deposit >= 1000, amount <= maxAmount]
    then
        modify($l) { setApproved(true) };
    end
    
    rule "LoanRule_16"
    when
        $l: /loanApplications[applicant.age >= 20, deposit >= 1000, amount > maxAmount]
    then
        modify($l) { setApproved(false) };
    end
    
    rule "LoanRule_17"
    when
        $l: /loanApplications[applicant.age < 20]
    then
        modify($l) { setApproved(false) };
    end
     */

    @Test
    public void decisionTable_basic() throws Exception {
        LoanUnit loanUnit = new LoanUnit();
        loanUnit.setMaxAmount(5000);

        try (RuleUnitInstance<LoanUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(loanUnit);) {
            Applicant applicant = new Applicant("John", 30);
            LoanApplication loanApplication = new LoanApplication("A001", applicant, 2000, 100);
            loanUnit.getLoanApplications().add(loanApplication);

            List<LoanApplication> queryResult = instance.executeQuery("FindApproved").toList("$l");

            assertThat(queryResult).containsExactlyInAnyOrder(loanApplication);
            assertThat(loanApplication.isApproved()).isTrue();
        }
    }

    @Test
    public void decisionTable_allRulesCoverage() throws Exception {
        LoanUnit loanUnit = new LoanUnit();
        loanUnit.setMaxAmount(5000);

        try (RuleUnitInstance<LoanUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(loanUnit);) {
            Applicant applicant1 = new Applicant("John", 30);
            LoanApplication loanApplication1 = new LoanApplication("A001", applicant1, 2000, 100);
            loanUnit.getLoanApplications().add(loanApplication1);
            Applicant applicant2 = new Applicant("Paul", 28);
            LoanApplication loanApplication2 = new LoanApplication("A002", applicant2, 5000, 100);
            loanUnit.getLoanApplications().add(loanApplication2);
            Applicant applicant3 = new Applicant("George", 27);
            LoanApplication loanApplication3 = new LoanApplication("A003", applicant3, 5000, 1000);
            loanUnit.getLoanApplications().add(loanApplication3);
            Applicant applicant4 = new Applicant("Ringo", 30);
            LoanApplication loanApplication4 = new LoanApplication("A004", applicant4, 8000, 1000);
            loanUnit.getLoanApplications().add(loanApplication4);
            Applicant applicant5 = new Applicant("Zak", 5);
            LoanApplication loanApplication5 = new LoanApplication("A005", applicant5, 2000, 100);
            loanUnit.getLoanApplications().add(loanApplication5);

            List<LoanApplication> queryResult = instance.executeQuery("FindApproved").toList("$l");

            assertThat(queryResult).containsExactlyInAnyOrder(loanApplication1, loanApplication3);
            assertThat(loanApplication1.isApproved()).isTrue();
            assertThat(loanApplication2.isApproved()).isFalse();
            assertThat(loanApplication3.isApproved()).isTrue();
            assertThat(loanApplication4.isApproved()).isFalse();
            assertThat(loanApplication5.isApproved()).isFalse();
        }
    }
}
