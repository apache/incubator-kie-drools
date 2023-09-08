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
package org.drools.ruleunit.example;

import java.util.List;
import java.util.Map;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class LoanTest {

    @Test
    public void test() {
        LoanUnit loanUnit = new LoanUnit();
        loanUnit.setMaxAmount(5000);

        RuleUnitInstance<LoanUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(loanUnit);

        loanUnit.getLoanApplications().add( new LoanApplication("ABC10002", new Applicant("Paul", 25), 5000, 100) );
        loanUnit.getLoanApplications().add( new LoanApplication("ABC10001", new Applicant("John", 45), 2000, 100) );
        loanUnit.getLoanApplications().add( new LoanApplication("ABC10015", new Applicant("George", 12), 1000, 100) );

        List<LoanApplication> results = instance.executeQuery("FindApproved").toList("$l");

        assertEquals(1, results.size());
        assertEquals("ABC10001", results.get(0).getId());
        assertEquals("John", results.get(0).getApplicant().getName());
    }
}
